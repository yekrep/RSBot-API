package org.powerbot.game.bot;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.powerbot.concurrent.ThreadPool;
import org.powerbot.event.EventDispatcher;
import org.powerbot.game.api.ActiveScript;
import org.powerbot.game.api.methods.input.Keyboard;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.widget.WidgetComposite;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.util.internal.Constants;
import org.powerbot.game.api.util.internal.Multipliers;
import org.powerbot.game.bot.event.PaintEvent;
import org.powerbot.game.bot.event.TextPaintEvent;
import org.powerbot.game.bot.handler.input.MouseExecutor;
import org.powerbot.game.bot.handler.input.util.MouseNode;
import org.powerbot.game.client.Client;
import org.powerbot.game.loader.ClientLoader;
import org.powerbot.game.loader.Loader;
import org.powerbot.game.loader.applet.ClientStub;
import org.powerbot.game.loader.applet.Rs2Applet;
import org.powerbot.game.loader.script.ModScript;
import org.powerbot.gui.BotChrome;
import org.powerbot.gui.component.BotPanel;
import org.powerbot.service.GameAccounts;
import org.powerbot.util.RestrictedSecurityManager;

/**
 * An environment of the game that is automated.
 *
 * @author Timer
 */
public final class Bot implements Runnable {
	private static final Logger log = Logger.getLogger(Bot.class.getName());
	private static Bot instance;

	public volatile Rs2Applet appletContainer;
	public volatile ClientStub stub;
	public Runnable callback;

	public ThreadGroup threadGroup;
	protected ThreadPoolExecutor container;

	private ClientLoader clientLoader;
	public final BotComposite composite;

	public ModScript modScript;
	private BotPanel panel;

	private GameAccounts.Account account;

	public BufferedImage image;
	private BufferedImage backBuffer;
	private final PaintEvent paintEvent;
	private final TextPaintEvent textPaintEvent;

	public volatile boolean refreshing;

	private Bot() {
		appletContainer = null;
		callback = null;
		stub = null;

		threadGroup = new ThreadGroup(Bot.class.getName() + "@" + hashCode());
		container = new ThreadPoolExecutor(1, Runtime.getRuntime().availableProcessors() * 2, 60, TimeUnit.HOURS, new SynchronousQueue<Runnable>(), new ThreadPool(threadGroup), new ThreadPoolExecutor.CallerRunsPolicy());

		composite = new BotComposite(this);
		panel = null;

		account = null;

		final Dimension d = new Dimension(BotChrome.PANEL_WIDTH, BotChrome.PANEL_HEIGHT);
		image = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
		backBuffer = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
		paintEvent = new PaintEvent();
		textPaintEvent = new TextPaintEvent();

		container.submit(composite.eventDispatcher);
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
		clientLoader = new ClientLoader();
		if (clientLoader.call()) {
			start();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Future<?> start() {
		if (clientLoader.isCancelled()) {
			return null;
		}
		log.info("Starting bot");
		final Context previous = composite.context;
		composite.context = new Context(this);
		if (previous != null) {
			WidgetComposite.clear(threadGroup);
			composite.context.world = previous.world;
		}
		Context.context.put(threadGroup, composite.context);
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

	public void reload() {
		log.info("Refreshing environment");
		if (composite.activeScript != null && composite.activeScript.isRunning()) {
			composite.activeScript.pause(true);
			while (composite.activeScript.getContainer().getActiveCount() > 0) {
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
		composite.client = null;
		BotChrome.getInstance().panel.setBot(this);

		clientLoader = new ClientLoader();
		if (clientLoader.call()) {
			final Future<?> future = start();
			if (future == null) {
				return;
			}
			try {
				future.get();
			} catch (final InterruptedException | ExecutionException ignored) {
			}

			if (composite.activeScript != null && composite.activeScript.isRunning()) {
				composite.activeScript.resume();
			}
		}

		refreshing = false;
	}

	/**
	 * {@inheritDoc}
	 */
	public void stop() {
		clientLoader.cancel();

		if (composite.activeScript != null) {
			composite.activeScript.stop();
			composite.activeScript.kill();
			composite.activeScript = null;
		}
		log.info("Unloading environment");
		if (composite.eventDispatcher != null) {
			composite.eventDispatcher.setActive(false);
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
		if (composite.client != null && panel != null && !BotChrome.minimised) {
			paintEvent.graphics = back;
			textPaintEvent.graphics = back;
			textPaintEvent.id = 0;
			composite.eventDispatcher.fire(paintEvent);
			composite.eventDispatcher.fire(textPaintEvent);
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
		this.composite.client = client;
		client.setCallback(new CallbackImpl(this));
		composite.constants = new Constants(modScript.constants);
		composite.multipliers = new Multipliers(modScript.multipliers);
		container.submit(new SafeMode(this));
		composite.executor = new MouseExecutor(this);

		composite.setup(composite.constants);
	}

	public Client getClient() {
		return composite.client;
	}

	public Context getContext() {
		return composite.context;
	}

	public ClientLoader getClientLoader() {
		return clientLoader;
	}

	/**
	 * @return The <code>Canvas</code> of this bot's client.
	 */
	public Canvas getCanvas() {
		return composite.client != null ? composite.client.getCanvas() : null;
	}

	public MouseExecutor getExecutor() {
		return composite.executor;
	}

	public EventDispatcher getEventDispatcher() {
		return composite.eventDispatcher;
	}

	public void setAccount(final GameAccounts.Account account) {
		this.account = account;
	}

	public GameAccounts.Account getAccount() {
		return account;
	}

	public ActiveScript getActiveScript() {
		return composite.activeScript;
	}

	public void ensureAntiRandoms() {
		RestrictedSecurityManager.assertNonScript();
		if (composite.antiRandomFuture == null || composite.antiRandomFuture.isDone()) {
			composite.antiRandomFuture = container.submit(composite.randomHandler);
		}
	}

	public void startScript(final ActiveScript script) {
		RestrictedSecurityManager.assertNonScript();
		if (composite.activeScript != null && composite.activeScript.isRunning()) {
			throw new RuntimeException("cannot run multiple scripts at once!");
		}

		this.composite.activeScript = script;
		script.init(composite.context);
		final Future<?> future = container.submit(script.start());
		try {
			future.get();
		} catch (final InterruptedException | ExecutionException ignored) {
		}
	}

	public void stopScript() {
		if (composite.activeScript == null) {
			throw new RuntimeException("script is non existent!");
		}

		log.info("Stopping script");
		composite.activeScript.stop();
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
			if (bot != null && !bot.clientLoader.isCancelled() && bot.getClient() != null && !Keyboard.isReady()) {
				while (!bot.clientLoader.isCancelled() && !Keyboard.isReady() && !Mouse.isReady()) {
					Time.sleep(1000);
				}
				Time.sleep(800);
				Keyboard.sendKey('s');
			}
		}
	}
}
