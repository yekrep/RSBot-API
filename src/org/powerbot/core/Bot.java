package org.powerbot.core;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;

import org.powerbot.core.bot.ClientStub;
import org.powerbot.core.bot.RSLoader;
import org.powerbot.core.bot.handlers.ScriptHandler;
import org.powerbot.core.event.EventManager;
import org.powerbot.core.event.events.PaintEvent;
import org.powerbot.core.event.events.TextPaintEvent;
import org.powerbot.core.script.internal.Constants;
import org.powerbot.core.script.job.Task;
import org.powerbot.game.api.methods.input.Keyboard;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.widget.WidgetCache;
import org.powerbot.game.bot.CallbackImpl;
import org.powerbot.game.bot.Context;
import org.powerbot.game.bot.handler.input.MouseExecutor;
import org.powerbot.game.bot.handler.input.util.MouseNode;
import org.powerbot.game.client.Client;
import org.powerbot.gui.BotChrome;
import org.powerbot.gui.component.BotPanel;
import org.powerbot.loader.script.ModScript;
import org.powerbot.service.GameAccounts;

/**
 * @author Timer
 */
public final class Bot implements Runnable {//TODO re-write bot
	static final Logger log = Logger.getLogger(Bot.class.getName());
	private static Bot instance;
	public final BotComposite composite;
	private final PaintEvent paintEvent;
	private final TextPaintEvent textPaintEvent;
	public volatile RSLoader appletContainer;
	public volatile ClientStub stub;
	public Runnable callback;
	public ThreadGroup threadGroup;
	public ModScript modScript;
	public BufferedImage image;
	public volatile boolean refreshing;
	private Client client;
	private Constants constants;
	private BotPanel panel;
	private GameAccounts.Account account;
	private BufferedImage backBuffer;

	private Bot() {
		appletContainer = new RSLoader();
		callback = null;
		stub = null;

		threadGroup = new ThreadGroup(Bot.class.getName() + "@" + hashCode());

		composite = new BotComposite(this);
		panel = null;

		account = null;

		final Dimension d = new Dimension(BotChrome.PANEL_WIDTH, BotChrome.PANEL_HEIGHT);
		image = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
		backBuffer = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
		paintEvent = new PaintEvent();
		textPaintEvent = new TextPaintEvent();

		new Thread(threadGroup, composite.eventManager, composite.eventManager.getClass().getName()).start();
		refreshing = false;
	}

	public synchronized static Bot instance() {
		if (instance == null) {
			instance = new Bot();
		}
		return instance;
	}

	public static boolean instantiated() {
		return instance != null;
	}

	public static Client client() {
		return instance.client;
	}

	public static Constants constants() {
		return instance.constants;
	}

	public static Context context() {
		return instance.composite.context;
	}

	public static void setSpeed(final Mouse.Speed speed) {
		final ThreadGroup group = Thread.currentThread().getThreadGroup();
		switch (speed) {
		case VERY_SLOW:
			MouseNode.speeds.put(group, 0.5d);
			break;
		case SLOW:
			MouseNode.speeds.put(group, 0.8d);
			break;
		case NORMAL:
			MouseNode.speeds.put(group, 1d);
			break;
		case FAST:
			MouseNode.speeds.put(group, 1.7d);
			break;
		case VERY_FAST:
			MouseNode.speeds.put(group, 2.5d);
			break;
		default:
			MouseNode.speeds.put(group, 1d);
			break;
		}
	}

	public void run() {
		start();
	}

	public void start() {
		log.info("Starting bot");
		final Context previous = composite.context;
		composite.context = new Context(this);
		if (previous != null) {
			WidgetCache.purge();
			composite.context.world = previous.world;
		}
		Context.context.put(threadGroup, composite.context);
		appletContainer.setCallback(new Runnable() {
			public void run() {
				setClient((Client) appletContainer.getClient());
				final Graphics graphics = image.getGraphics();
				appletContainer.update(graphics);
				graphics.dispose();
				resize(BotChrome.PANEL_WIDTH, BotChrome.PANEL_HEIGHT);
			}
		});

		appletContainer.load();
		stub = new ClientStub(appletContainer);
		appletContainer.setStub(stub);
		stub.setApplet(appletContainer);
		stub.setActive(true);
		log.info("Starting game");
		new Thread(threadGroup, appletContainer, "Loader").start();
	}

	/**
	 * {@inheritDoc}
	 */
	public void stop() {
		if (composite.scriptHandler != null) {
			composite.scriptHandler.stop();
		}
		log.info("Unloading environment");
		if (composite.eventManager != null) {
			composite.eventManager.stop();
		}
		new Thread(threadGroup, new Runnable() {
			@Override
			public void run() {
				terminateApplet();
			}
		}).start();
		Context.context.remove(threadGroup);
		instance = null;
	}

	void terminateApplet() {
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
			client = null;
		}
	}

	public void stopScript() {
		if (composite.scriptHandler == null) {
			throw new RuntimeException("script is non existent!");
		}

		log.info("Stopping script");
		composite.scriptHandler.shutdown();
	}

	public BufferedImage getImage() {
		return image;
	}

	public BufferedImage getBuffer() {
		return backBuffer;
	}

	public void resize(final int width, final int height) {
		backBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		if (appletContainer != null) {
			appletContainer.setSize(width, height);
			final Graphics buffer = backBuffer.getGraphics();
			appletContainer.update(buffer);
			buffer.dispose();
		}
	}

	public Graphics getBufferGraphics() {
		final Graphics back = backBuffer.getGraphics();
		if (client != null && panel != null && !BotChrome.minimised) {
			paintEvent.graphics = back;
			textPaintEvent.graphics = back;
			textPaintEvent.id = 0;
			try {
				composite.eventManager.fire(paintEvent);
				composite.eventManager.fire(textPaintEvent);
			} catch (final Exception e) {
				e.printStackTrace();
			}
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

	public void setPanel(final BotPanel panel) {
		this.panel = panel;
	}

	private void setClient(final Client client) {
		this.client = client;
		client.setCallback(new CallbackImpl(this));
		constants = new Constants(modScript.constants);
		new Thread(threadGroup, new SafeMode(this)).start();
		composite.executor = new MouseExecutor();
	}

	public Context getContext() {
		return composite.context;
	}

	public Canvas getCanvas() {
		return client != null ? client.getCanvas() : null;
	}

	public MouseExecutor getMouseExecutor() {
		return composite.executor;
	}

	public EventManager getEventManager() {
		return composite.eventManager;
	}

	public GameAccounts.Account getAccount() {
		return account;
	}

	public void setAccount(final GameAccounts.Account account) {
		this.account = account;
	}

	public ScriptHandler getScriptHandler() {
		return composite.scriptHandler;
	}

	public synchronized void refresh() {
		if (refreshing) {
			return;
		}

		refreshing = true;
		new Thread(threadGroup, new Runnable() {
			public void run() {
				composite.reload();
			}
		}).start();
	}

	private static final class SafeMode implements Runnable {
		private final Bot bot;

		public SafeMode(final Bot bot) {
			this.bot = bot;
		}

		public void run() {
			if (bot != null && bot.client != null && !Keyboard.isReady()) {
				while (!Keyboard.isReady() && !Mouse.isReady()) {
					Task.sleep(1000);
				}
				Task.sleep(800);
				Keyboard.sendKey('s');
			}
		}
	}
}
