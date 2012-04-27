package org.powerbot.game.bot;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.powerbot.asm.NodeManipulator;
import org.powerbot.concurrent.Task;
import org.powerbot.event.EventDispatcher;
import org.powerbot.game.GameDefinition;
import org.powerbot.game.api.ActiveScript;
import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.input.Keyboard;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.util.internal.Constants;
import org.powerbot.game.api.util.internal.Multipliers;
import org.powerbot.game.bot.event.PaintEvent;
import org.powerbot.game.bot.event.TextPaintEvent;
import org.powerbot.game.bot.handler.RandomHandler;
import org.powerbot.game.bot.handler.input.MouseReactor;
import org.powerbot.game.client.Client;
import org.powerbot.game.client.Model;
import org.powerbot.game.client.ModelCapture;
import org.powerbot.game.client.Render;
import org.powerbot.game.client.RenderAbsoluteX;
import org.powerbot.game.client.RenderAbsoluteY;
import org.powerbot.game.client.RenderData;
import org.powerbot.game.client.RenderFloats;
import org.powerbot.game.client.RenderRenderData;
import org.powerbot.game.client.RenderXMultiplier;
import org.powerbot.game.client.RenderYMultiplier;
import org.powerbot.game.loader.AdaptException;
import org.powerbot.game.loader.Loader;
import org.powerbot.game.loader.script.ModScript;
import org.powerbot.gui.BotChrome;
import org.powerbot.gui.component.BotPanel;
import org.powerbot.service.GameAccounts;
import org.powerbot.util.io.HttpClient;
import org.powerbot.util.io.IOHelper;
import org.powerbot.util.io.Resources;

/**
 * An environment of the game that is automated.
 *
 * @author Timer
 */
public class Bot extends GameDefinition implements Runnable {
	private static Logger log = Logger.getLogger(Bot.class.getName());
	public static final LinkedList<Bot> bots = new LinkedList<Bot>();

	private ModScript modScript;
	private BotPanel panel;
	private Client client;
	public Constants constants;
	public Multipliers multipliers;
	public final Calculations.Toolkit toolkit;
	public final Calculations.Viewport viewport;
	public final Map<Object, Model> modelCache;
	private final Timer modelCleaner;
	private final TimerTask cleanModels;

	private MouseReactor reactor;
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

	public Bot() {
		final Dimension d = new Dimension(BotChrome.PANEL_WIDTH, BotChrome.PANEL_HEIGHT);
		image = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
		backBuffer = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
		client = null;
		panel = null;
		paintEvent = new PaintEvent();
		textPaintEvent = new TextPaintEvent();
		reactor = null;
		eventDispatcher = new EventDispatcher();
		container.submit(eventDispatcher);
		toolkit = new Calculations.Toolkit();
		viewport = new Calculations.Viewport();
		modelCache = Collections.synchronizedMap(new HashMap<Object, Model>());
		activeScript = null;
		randomHandler = new RandomHandler(this);
		antiRandomFuture = null;
		account = null;
		refreshing = false;

		modelCleaner = new Timer("model-cleanser " + threadGroup.getName(), true);
		cleanModels = new TimerTask() {
			@Override
			public void run() {
				container.submit(new Task() {
					@Override
					public void run() {
						try {
							final int size_1 = modelCache.size();
							final long mark_1 = System.currentTimeMillis();
							ModelCapture.clean();
							final long mark_2 = System.currentTimeMillis();
							final int size_2 = modelCache.size();
							log.fine("Cleansed models in " + (mark_2 - mark_1) + "ms (count " + size_1 + " -> " + size_2 + ")");
						} catch (final Throwable ignored) {
						}
					}
				});
			}
		};
	}

	/**
	 * Initializes this bot and adds it to reference.
	 */
	public void run() {
		Bot.bots.add(this);
		if (initializeEnvironment()) {
			startEnvironment();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Future<?> startEnvironment() {
		if (killed) {
			return null;
		}
		log.info("Starting bot");
		context = new Context(this);
		Context.context.put(threadGroup, context);
		callback = new Runnable() {
			public void run() {
				modelCleaner.schedule(cleanModels, 60000, 30000);
				setClient((Client) appletContainer.clientInstance);
				appletContainer.paint(image.getGraphics());
				resize(BotChrome.PANEL_WIDTH, BotChrome.PANEL_HEIGHT);
			}
		};
		log.fine("Submitting loader");
		return container.submit(new Loader(this));
	}

	public void refreshEnvironment() {
		log.info("Refreshing environment");
		if (activeScript != null && activeScript.isRunning()) {
			activeScript.pause(true);
			while (activeScript.getContainer().isActive()) {
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

		if (initializeEnvironment()) {
			final Future<?> future = startEnvironment();
			if (future == null) {
				return;
			}
			try {
				future.get();
			} catch (final InterruptedException ignored) {
			} catch (final ExecutionException ignored) {
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
			modScript = new ModScript(IOHelper.read(HttpClient.openStream(new URL(String.format(Resources.getServerLinks().get("clientpatch"), packHash)))));
			return modScript;
		} catch (final SocketTimeoutException ignored) {
			log.severe("Please try again later " + id);
		} catch (final NullPointerException ignored) {
			log.severe("Please try again later " + id);
		} catch (final IOException e) {
			log.log(Level.FINE, "Failed to get node manipulator: ", e);
		}
		throw new AdaptException("Failed to load node manipulator; unable to reach server or client unsupported");
	}

	/**
	 * {@inheritDoc}
	 */
	public void killEnvironment() {
		this.killed = true;
		modelCleaner.cancel();
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
		Task task = null;
		if (appletContainer != null) {
			log.fine("Shutting down applet");
			task = new Task() {
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
		bots.remove(this);
		Context.context.remove(threadGroup);
		container.shutdown();
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
		appletContainer.update(backBuffer.getGraphics());
		appletContainer.paint(backBuffer.getGraphics());
	}

	/**
	 * Returns the buffered graphics associated with this bot's client after painting information provided by events.
	 *
	 * @return The <code>Graphics</code> to be displayed in the <code>Canvas</code>.
	 */
	public Graphics getBufferGraphics() {
		final Graphics back = backBuffer.getGraphics();
		if (client != null) {
			paintEvent.graphics = back;
			textPaintEvent.graphics = back;
			textPaintEvent.id = 0;
			eventDispatcher.fire(paintEvent);
			eventDispatcher.fire(textPaintEvent);
		}
		back.dispose();
		image.getGraphics().drawImage(backBuffer, 0, 0, null);
		if (panel != null) {
			panel.repaint();
		}
		return backBuffer.getGraphics();
	}

	public BufferedImage getImage() {
		return image;
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
		container.submit(reactor = new MouseReactor(this));
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

	public MouseReactor getReactor() {
		return reactor;
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
		if (antiRandomFuture == null || antiRandomFuture.isDone()) {
			antiRandomFuture = container.submit(randomHandler);
		}
	}

	private void validateAccount() {
		if (client != null) {
			final String username = GameAccounts.normaliseUsername(client.getCurrentUsername());
			final String password = client.getCurrentPassword();
			if (username.isEmpty() || password.isEmpty()) {
				return;
			}

			final GameAccounts gameAccounts = GameAccounts.getInstance();
			try {
				gameAccounts.load();
			} catch (final IOException ignored) {
			} catch (final GeneralSecurityException ignored) {
			}
			final GameAccounts.Account stored_account;
			if ((stored_account = gameAccounts.get(username)) == null) {
				if (gameAccounts.get(password) != null) {
					return;
				}
				final GameAccounts.Account account = gameAccounts.add(username);
				account.setPassword(password);
				try {
					gameAccounts.save();
				} catch (final IOException ignored) {
				} catch (final GeneralSecurityException ignored) {
				}
			} else {
				if (!stored_account.getPassword().equals(password)) {
					stored_account.setPassword(password);
					try {
						gameAccounts.save();
					} catch (final IOException ignored) {
					} catch (final GeneralSecurityException ignored) {
					}
				}
			}
		}
	}

	public void startScript(final ActiveScript script) {
		if (activeScript != null && activeScript.isRunning()) {
			throw new RuntimeException("cannot run multiple scripts at once!");
		}

		this.activeScript = script;
		script.init(context);
		final Future<?> future = container.submit(script.start());
		try {
			future.get();
		} catch (InterruptedException ignored) {
		} catch (ExecutionException ignored) {
		}

		container.submit(new Task() {
			@Override
			public void run() {
				validateAccount();
			}
		});
	}

	public void stopScript() {
		if (activeScript == null) {
			throw new RuntimeException("script is non existent!");
		}

		log.info("Stopping script");
		activeScript.stop();
	}

	public void refresh() {
		refreshing = true;
		container.submit(new Task() {
			public void run() {
				refreshEnvironment();
			}
		});
	}

	public void updateToolkit(final Render render) {
		final Object renderData = render.getData();
		final Object toolkit = ((RenderFloats) renderData).getRenderFloats();
		this.toolkit.absoluteX = ((RenderAbsoluteX) toolkit).getRenderAbsoluteX();
		this.toolkit.absoluteY = ((RenderAbsoluteY) toolkit).getRenderAbsoluteY();
		this.toolkit.xMultiplier = ((RenderXMultiplier) toolkit).getRenderXMultiplier();
		this.toolkit.yMultiplier = ((RenderYMultiplier) toolkit).getRenderYMultiplier();
		final float[] viewport = ((RenderData) ((RenderRenderData) renderData).getRenderRenderData()).getFloats();
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

	public static Bot resolve(final Object o) {
		final ClassLoader cl = o.getClass().getClassLoader();
		for (final Bot bot : Bot.bots) {
			if (cl == bot.getClient().getClass().getClassLoader()) {
				return bot;
			}
		}
		return null;
	}

	/**
	 * @author Timer
	 */
	private static final class SafeMode implements Task {
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
