package org.powerbot.game.bot;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.powerbot.asm.NodeManipulator;
import org.powerbot.concurrent.ThreadPool;
import org.powerbot.event.EventDispatcher;
import org.powerbot.game.api.ActiveScript;
import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.input.Keyboard;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.widget.WidgetComposite;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.util.internal.Constants;
import org.powerbot.game.api.util.internal.Multipliers;
import org.powerbot.game.bot.event.PaintEvent;
import org.powerbot.game.bot.event.TextPaintEvent;
import org.powerbot.game.bot.handler.RandomHandler;
import org.powerbot.game.bot.handler.input.MouseExecutor;
import org.powerbot.game.bot.handler.input.util.MouseNode;
import org.powerbot.game.client.Client;
import org.powerbot.game.client.Render;
import org.powerbot.game.client.RenderData;
import org.powerbot.game.loader.AdaptException;
import org.powerbot.game.loader.Crawler;
import org.powerbot.game.loader.Crypt;
import org.powerbot.game.loader.Deflator;
import org.powerbot.game.loader.Loader;
import org.powerbot.game.loader.applet.ClientStub;
import org.powerbot.game.loader.applet.Rs2Applet;
import org.powerbot.game.loader.script.ModScript;
import org.powerbot.gui.BotChrome;
import org.powerbot.gui.component.BotPanel;
import org.powerbot.service.GameAccounts;
import org.powerbot.util.Configuration;
import org.powerbot.util.RestrictedSecurityManager;
import org.powerbot.util.StringUtil;
import org.powerbot.util.io.HttpClient;
import org.powerbot.util.io.IOHelper;

/**
 * An environment of the game that is automated.
 *
 * @author Timer
 * @author Paris
 */
public final class Bot implements Runnable {
	private static final Logger log = Logger.getLogger(Bot.class.getName());
	private static Bot instance;

	protected ThreadPoolExecutor container;
	private final Map<String, byte[]> classes;
	public static final String THREADGROUPNAMEPREFIX = "GameDefinition-";

	public Crawler crawler;
	public volatile Rs2Applet appletContainer;
	public Runnable callback;
	public volatile ClientStub stub;
	protected String packHash;
	public ThreadGroup threadGroup;
	protected volatile boolean killed;

	private ModScript modScript;
	private BotPanel panel;
	private Client client;
	public Constants constants;
	public Multipliers multipliers;
	public final Calculations.Toolkit toolkit;
	public final Calculations.Viewport viewport;

	private MouseExecutor executor;
	private EventDispatcher eventDispatcher;
	private ActiveScript activeScript;
	private RandomHandler randomHandler;
	private Future<?> antiRandomFuture;
	private Context context;

	private GameAccounts.Account account;

	public BufferedImage image;
	private BufferedImage backBuffer;
	private final PaintEvent paintEvent;
	private final TextPaintEvent textPaintEvent;

	public volatile boolean refreshing;

	private Bot() {
		threadGroup = new ThreadGroup(THREADGROUPNAMEPREFIX + hashCode());
		container = new ThreadPoolExecutor(1, Runtime.getRuntime().availableProcessors() * 2, 60, TimeUnit.HOURS, new SynchronousQueue<Runnable>(), new ThreadPool(threadGroup), new ThreadPoolExecutor.CallerRunsPolicy());
		classes = new HashMap<>();

		crawler = new Crawler();
		appletContainer = null;
		callback = null;
		stub = null;
		packHash = null;
		killed = false;

		final Dimension d = new Dimension(BotChrome.PANEL_WIDTH, BotChrome.PANEL_HEIGHT);
		image = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
		backBuffer = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
		client = null;
		panel = null;
		paintEvent = new PaintEvent();
		textPaintEvent = new TextPaintEvent();
		executor = null;
		eventDispatcher = new EventDispatcher();
		container.submit(eventDispatcher);
		toolkit = new Calculations.Toolkit();
		viewport = new Calculations.Viewport();
		activeScript = null;
		randomHandler = new RandomHandler(this);
		antiRandomFuture = null;
		account = null;
		refreshing = false;
	}

	public synchronized static Bot getInstance() {
		if (instance == null) {
			instance = new Bot();
		}
		return instance;
	}

	public synchronized static boolean isInstantiated() {
		return instance != null;
	}

	/**
	 * Initializes this bot and adds it to reference.
	 */
	public void run() {
		BotChrome.getInstance().toolbar.updateScriptControls();
		if (call()) {
			start();
		}
	}

	public Boolean call() {
		this.killed = false;
		log.info("Initializing game environment");
		classes.clear();
		log.fine("Crawling (for) game information");
		if (!crawler.crawl()) {
			log.severe("Please try again");
			return false;
		}
		if (killed) {
			return false;
		}
		log.fine("Downloading loader");
		final byte[] loader = getLoader(crawler);
		if (loader != null) {
			final String secretKeySpecKey = crawler.parameters.get("0");
			final String ivParameterSpecKey = crawler.parameters.get("-1");
			if (secretKeySpecKey == null || ivParameterSpecKey == null) {
				log.fine("Invalid secret spec key and/or iv parameter spec key");
				return false;
			}
			if (killed) {
				return false;
			}
			log.fine("Removing key ciphering");
			final byte[] secretKeySpecBytes = Crypt.decode(secretKeySpecKey);
			final byte[] ivParameterSpecBytes = Crypt.decode(ivParameterSpecKey);
			log.fine("Extracting classes from loader");
			final Map<String, byte[]> classes = Deflator.extract(secretKeySpecBytes, ivParameterSpecBytes, loader);
			log.fine("Generating client hash");
			packHash = StringUtil.byteArrayToHexString(Deflator.inner_pack_hash);
			log.fine("Client hash (" + packHash + ")");
			if (classes != null && classes.size() > 0) {
				this.classes.putAll(classes);
				classes.clear();
			}
			if (killed) {
				return false;
			}
			if (this.classes.size() > 0) {
				NodeManipulator nodeManipulator;
				try {
					nodeManipulator = getNodeManipulator();
				} catch (final Throwable e) {
					log.log(Level.FINE, "Failed to load manipulator: ", e);
					return false;
				}
				if (nodeManipulator != null) {
					log.fine("Running node manipulator");
					try {
						nodeManipulator.adapt();
					} catch (final AdaptException e) {
						log.log(Level.FINE, "Node manipulation failed", e);
						return false;
					}
					log.fine("Processing classes");
					for (final Map.Entry<String, byte[]> clazz : this.classes.entrySet()) {
						final String name = clazz.getKey();
						this.classes.put(name, nodeManipulator.process(name, clazz.getValue()));
					}
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public Future<?> start() {
		if (killed) {
			return null;
		}
		log.info("Starting bot");
		final Context previous = context;
		context = new Context(this);
		if (previous != null) {
			WidgetComposite.clear(threadGroup);
			context.world = previous.world;
		}
		Context.context.put(threadGroup, context);
		callback = new Runnable() {
			public void run() {
				setClient((Client) appletContainer.clientInstance);
				final Graphics graphics = image.getGraphics();
				appletContainer.update(graphics);
				graphics.dispose();
				resize(BotChrome.PANEL_WIDTH, BotChrome.PANEL_HEIGHT);
			}
		};
		log.fine("Submitting loader");
		return container.submit(new Loader(this));
	}

	public ThreadPoolExecutor getContainer() {
		return container;
	}

	public static byte[] getLoader(final Crawler crawler) {
		try {
			final URLConnection clientConnection = HttpClient.getHttpConnection(new URL(crawler.archive));
			clientConnection.addRequestProperty("Referer", crawler.game);
			return IOHelper.read(HttpClient.getInputStream(clientConnection));
		} catch (final IOException ignored) {
		}
		return null;
	}

	public Map<String, byte[]> classes() {
		final Map<String, byte[]> classes = new HashMap<>();
		classes.putAll(this.classes);
		return classes;
	}

	public void reload() {
		log.info("Refreshing environment");
		if (activeScript != null && activeScript.isRunning()) {
			activeScript.pause(true);
			while (activeScript.getContainer().getActiveCount() > 0) {
				Time.sleep(150);
			}
		}

		if (stub != null) {
			log.fine("Terminating stub activities");
			stub.setActive(false);
		}
		if (appletContainer != null) {
			log.fine("Shutting down applet");
			appletContainer.stop();
			appletContainer.destroy();
			appletContainer = null;
			stub = null;
		}
		final Dimension d = new Dimension(BotChrome.PANEL_WIDTH, BotChrome.PANEL_HEIGHT);
		image = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
		backBuffer = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
		client = null;
		BotChrome.getInstance().panel.setBot(this);

		if (call()) {
			final Future<?> future = start();
			if (future == null) {
				return;
			}
			try {
				future.get();
			} catch (final InterruptedException | ExecutionException ignored) {
			}

			if (activeScript != null && activeScript.isRunning()) {
				activeScript.resume();
			}
		}

		refreshing = false;
	}

	/**
	 * {@inheritDoc}
	 */
	public NodeManipulator getNodeManipulator() throws AdaptException {
		final String id = "(" + packHash.substring(0, 6) + ")";
		log.info("Loading client patch " + id);
		try {
			modScript = new ModScript(IOHelper.read(HttpClient.openStream(new URL(String.format(Configuration.URLs.CLIENTPATCH, packHash)))));
			return modScript;
		} catch (final SocketTimeoutException ignored) {
			log.severe("Cannot connect to update server " + id);
		} catch (final NullPointerException ignored) {
			log.severe("Error parsing client patch " + id);
		} catch (final IOException e) {
			log.log(Level.SEVERE, "Client patch " + id + " unavailable", "Outdated");
		}
		throw new AdaptException("Failed to load node manipulator; unable to reach server or client unsupported");
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() {
		this.killed = true;
		if (activeScript != null) {
			activeScript.stop();
			activeScript.kill();
			activeScript = null;
		}
		log.info("Unloading environment");
		if (eventDispatcher != null) {
			eventDispatcher.setActive(false);
		}
		if (stub != null) {
			log.fine("Terminating stub activities");
			stub.setActive(false);
		}
		Runnable task = null;
		if (appletContainer != null) {
			log.fine("Shutting down applet");
			task = new Runnable() {
				public void run() {
					appletContainer.stop();
					appletContainer.destroy();
					appletContainer = null;
					stub = null;
				}
			};
		}
		if (task != null) {
			container.submit(task);
		}
		Context.context.remove(threadGroup);
		container.shutdown();
		instance = null;
	}

	/**
	 * Sets the panel currently associated with this bot to relay events to.
	 *
	 * @param panel The <code>BotPanel</code> responsible for displaying this bot.
	 */
	public void setPanel(final BotPanel panel) {
		this.panel = panel;
	}

	/**
	 * Resizes this bot's back buffer and container.
	 *
	 * @param width  Width of the component.
	 * @param height Height of the component.
	 */
	public void resize(final int width, final int height) {
		backBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		appletContainer.setSize(width, height);
		final Graphics buffer = backBuffer.getGraphics();
		appletContainer.update(buffer);
		buffer.dispose();
	}

	/**
	 * Returns the buffered graphics associated with this bot's client after painting information provided by events.
	 *
	 * @return The <code>Graphics</code> to be displayed in the <code>Canvas</code>.
	 */
	public Graphics getBufferGraphics() {
		final Graphics back = backBuffer.getGraphics();
		if (client != null && panel != null && !BotChrome.minimised) {
			paintEvent.graphics = back;
			textPaintEvent.graphics = back;
			textPaintEvent.id = 0;
			eventDispatcher.fire(paintEvent);
			eventDispatcher.fire(textPaintEvent);
		}
		back.dispose();
		final Graphics imageGraphics = image.getGraphics();
		imageGraphics.drawImage(backBuffer, 0, 0, null);
		imageGraphics.dispose();
		if (panel != null) {
			panel.repaint();
		}
		return backBuffer.getGraphics();
	}

	public BufferedImage getImage() {
		return image;
	}

	public BufferedImage getBuffer() {
		return backBuffer;
	}

	/**
	 * Sets the client of this bot to the provided <code>Client</code>, while initializing it to be associated with this bot via callback.
	 *
	 * @param client The <code>Client</code> to associate with this bot.
	 */
	private void setClient(final Client client) {
		this.client = client;
		client.setCallback(new CallbackImpl(this));
		constants = new Constants(modScript.constants);
		multipliers = new Multipliers(modScript.multipliers);
		container.submit(new SafeMode(this));
		executor = new MouseExecutor(this);
	}

	public Client getClient() {
		return client;
	}

	public Context getContext() {
		return context;
	}

	/**
	 * @return The <code>Canvas</code> of this bot's client.
	 */
	public Canvas getCanvas() {
		return client != null ? client.getCanvas() : null;
	}

	public MouseExecutor getExecutor() {
		return executor;
	}

	public EventDispatcher getEventDispatcher() {
		return eventDispatcher;
	}

	public void setAccount(final GameAccounts.Account account) {
		this.account = account;
	}

	public GameAccounts.Account getAccount() {
		return account;
	}

	public ActiveScript getActiveScript() {
		return activeScript;
	}

	public void ensureAntiRandoms() {
		RestrictedSecurityManager.assertNonScript();
		if (antiRandomFuture == null || antiRandomFuture.isDone()) {
			antiRandomFuture = container.submit(randomHandler);
		}
	}

	public void startScript(final ActiveScript script) {
		RestrictedSecurityManager.assertNonScript();
		if (activeScript != null && activeScript.isRunning()) {
			throw new RuntimeException("cannot run multiple scripts at once!");
		}

		this.activeScript = script;
		script.init(context);
		final Future<?> future = container.submit(script.start());
		try {
			future.get();
		} catch (final InterruptedException | ExecutionException ignored) {
		}
	}

	public void stopScript() {
		if (activeScript == null) {
			throw new RuntimeException("script is non existent!");
		}

		log.info("Stopping script");
		activeScript.stop();
	}

	public void refresh() {
		if (refreshing) {
			return;
		}
		refreshing = true;
		container.submit(new Runnable() {
			public void run() {
				reload();
			}
		});
	}

	public static void setSpeed(final int speed) {
		MouseNode.threadSpeed.put(Thread.currentThread().getThreadGroup(), speed);
	}

	public void updateToolkit(final Render render) {
		this.toolkit.absoluteX = render.getAbsoluteX();
		this.toolkit.absoluteY = render.getAbsoluteY();
		this.toolkit.xMultiplier = render.getXMultiplier();
		this.toolkit.yMultiplier = render.getYMultiplier();
		final RenderData toolkit = render.getViewport();
		final float[] viewport = toolkit.getFloats();
		this.viewport.xOff = viewport[constants.VIEWPORT_XOFF];
		this.viewport.xX = viewport[constants.VIEWPORT_XX];
		this.viewport.xY = viewport[constants.VIEWPORT_XY];
		this.viewport.xZ = viewport[constants.VIEWPORT_XZ];
		this.viewport.yOff = viewport[constants.VIEWPORT_YOFF];
		this.viewport.yX = viewport[constants.VIEWPORT_YX];
		this.viewport.yY = viewport[constants.VIEWPORT_YY];
		this.viewport.yZ = viewport[constants.VIEWPORT_YZ];
		this.viewport.zOff = viewport[constants.VIEWPORT_ZOFF];
		this.viewport.zX = viewport[constants.VIEWPORT_ZX];
		this.viewport.zY = viewport[constants.VIEWPORT_ZY];
		this.viewport.zZ = viewport[constants.VIEWPORT_ZZ];
	}

	/**
	 * @author Timer
	 */
	private static final class SafeMode implements Runnable {
		private final Bot bot;

		public SafeMode(final Bot bot) {
			this.bot = bot;
		}

		/**
		 * Enters the game into SafeMode by pressing 's'.
		 */
		public void run() {
			if (bot != null && !bot.killed && bot.getClient() != null && !Keyboard.isReady()) {
				while (!bot.killed && !Keyboard.isReady() && !Mouse.isReady()) {
					Time.sleep(1000);
				}
				Time.sleep(800);
				Keyboard.sendKey('s');
			}
		}
	}
}
