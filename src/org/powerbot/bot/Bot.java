package org.powerbot.bot;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;

import org.powerbot.core.script.job.Task;
import org.powerbot.event.EventMulticaster;
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
import org.powerbot.gui.controller.BotInteract;
import org.powerbot.loader.script.ModScript;
import org.powerbot.script.Script;
import org.powerbot.script.event.PaintEvent;
import org.powerbot.script.event.TextPaintEvent;
import org.powerbot.script.internal.Constants;
import org.powerbot.script.internal.ScriptController;
import org.powerbot.script.internal.input.MouseHandler;
import org.powerbot.service.GameAccounts;
import org.powerbot.service.scripts.ScriptDefinition;

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
	public volatile BotStub stub;
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
	private MouseHandler mouseHandler;
	private EventMulticaster multicaster;
	private MouseExecutor oldMouse;
	private ScriptController scriptController;
	private ScriptDefinition scriptDefinition;

	private Bot() {
		appletContainer = null;
		callback = null;
		stub = null;

		threadGroup = new ThreadGroup(Bot.class.getName() + "@" + hashCode());

		composite = new BotComposite(this);
		multicaster = new EventMulticaster();
		panel = null;

		account = null;

		final Dimension d = new Dimension(BotChrome.PANEL_WIDTH, BotChrome.PANEL_HEIGHT);
		image = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
		backBuffer = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
		paintEvent = new PaintEvent();
		textPaintEvent = new TextPaintEvent();

		new Thread(threadGroup, multicaster, multicaster.getClass().getName()).start();
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

	public static MouseHandler mouseHandler() {
		return instance.mouseHandler;
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
		BotChrome.getInstance().toolbar.updateControls();
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
		appletContainer = new RSLoader();
		appletContainer.setCallback(new Runnable() {
			public void run() {
				setClient((Client) appletContainer.getClient());
				final Graphics graphics = image.getGraphics();
				appletContainer.update(graphics);
				graphics.dispose();
				resize(BotChrome.PANEL_WIDTH, BotChrome.PANEL_HEIGHT);
			}
		});

		if (!appletContainer.load()) {
			BotInteract.tabClose(true);
			return;
		}
		stub = new BotStub(appletContainer, appletContainer.getClientLoader().crawler);
		appletContainer.setStub(stub);
		stub.setActive(true);
		log.info("Starting game");
		new Thread(threadGroup, appletContainer, "Loader").start();
	}

	/**
	 * {@inheritDoc}
	 */
	public void stop() {
		if (mouseHandler != null) mouseHandler().stop();
		if (scriptController != null) {
			scriptController.close();
		}
		log.info("Unloading environment");
		if (multicaster != null) {
			multicaster.stop();
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

	public void startScript(final Script script, final ScriptDefinition definition) {
		scriptController = new ScriptController(); // TODO: multicaster?!
		scriptController.getScripts().add(script);
		scriptDefinition = definition;
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
				multicaster.fire(paintEvent);
				multicaster.fire(textPaintEvent);
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
		oldMouse = new MouseExecutor();
		mouseHandler = new MouseHandler(appletContainer, client);
		new Thread(threadGroup, mouseHandler).start();
	}

	public Context getContext() {
		return composite.context;
	}

	public Canvas getCanvas() {
		return client != null ? client.getCanvas() : null;
	}

	public MouseExecutor getMouseExecutor() {
		return oldMouse;
	}

	public EventMulticaster getEventMulticaster() {
		return multicaster;
	}

	public GameAccounts.Account getAccount() {
		return account;
	}

	public void setAccount(final GameAccounts.Account account) {
		this.account = account;
	}

	public ScriptController getScriptController() {
		return this.scriptController;
	}

	public ScriptDefinition getScriptDefinition() {
		return this.scriptDefinition;
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
