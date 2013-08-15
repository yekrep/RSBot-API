package org.powerbot.bot;

import java.applet.Applet;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import org.powerbot.bot.loader.Crawler;
import org.powerbot.bot.loader.GameLoader;
import org.powerbot.bot.loader.GameStub;
import org.powerbot.bot.loader.NRSLoader;
import org.powerbot.bot.loader.transform.TransformSpec;
import org.powerbot.client.Client;
import org.powerbot.client.Constants;
import org.powerbot.event.EventMulticaster;
import org.powerbot.event.PaintEvent;
import org.powerbot.event.TextPaintEvent;
import org.powerbot.gui.BotChrome;
import org.powerbot.gui.component.BotPanel;
import org.powerbot.script.Script;
import org.powerbot.script.internal.InputHandler;
import org.powerbot.script.internal.MouseHandler;
import org.powerbot.script.internal.ScriptController;
import org.powerbot.script.lang.Stoppable;
import org.powerbot.script.methods.Game;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.util.Random;
import org.powerbot.service.GameAccounts;
import org.powerbot.service.scripts.ScriptDefinition;

/**
 * @author Timer
 */
public final class Bot implements Runnable, Stoppable {//TODO re-write bot
	public static final Logger log = Logger.getLogger(Bot.class.getName());
	private MethodContext ctx;
	public final ThreadGroup threadGroup;
	private final PaintEvent paintEvent;
	private final TextPaintEvent textPaintEvent;
	private final EventMulticaster multicaster;
	private volatile Applet appletContainer;
	private BufferedImage image;
	public AtomicBoolean refreshing;
	private Constants constants;
	private BotPanel panel;
	private GameAccounts.Account account;
	private BufferedImage backBuffer;
	private MouseHandler mouseHandler;
	private InputHandler inputHandler;
	private ScriptController controller;
	private boolean stopping;

	public Bot() {
		appletContainer = null;

		threadGroup = new ThreadGroup(Bot.class.getName() + "@" + Integer.toHexString(hashCode()) + "-game");

		multicaster = new EventMulticaster();
		panel = null;

		account = null;

		final Dimension d = new Dimension(BotChrome.PANEL_MIN_WIDTH, BotChrome.PANEL_MIN_HEIGHT);
		image = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
		backBuffer = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
		paintEvent = new PaintEvent();
		textPaintEvent = new TextPaintEvent();

		new Thread(threadGroup, multicaster, multicaster.getClass().getName()).start();
		refreshing = new AtomicBoolean(false);

		ctx = new MethodContext(this);
	}

	public void run() {
		start();
	}

	public void start() {
		//TODO: prompt user when this fails.
		log.info("Loading bot");
		Crawler crawler = new Crawler();
		if (!crawler.crawl()) {
			return;
		}

		GameLoader game = new GameLoader(crawler);
		ClassLoader classLoader = game.call();
		if (classLoader == null) {
			return;
		}

		final NRSLoader loader = new NRSLoader(this, game, classLoader);
		loader.setCallback(new Runnable() {
			@Override
			public void run() {
				sequence(loader);
			}
		});
		new Thread(threadGroup, loader).start();
	}

	private void sequence(final NRSLoader loader) {
		log.info("Loading game (" + loader.getPackHash().substring(0, 6) + ")");
		this.appletContainer = loader.getApplet();
		Crawler crawler = loader.getGameLoader().getCrawler();
		GameStub stub = new GameStub(crawler.parameters, crawler.archive);
		appletContainer.setStub(stub);

		final Graphics graphics = image.getGraphics();
		appletContainer.update(graphics);
		graphics.dispose();
		resize(BotChrome.PANEL_MIN_WIDTH, BotChrome.PANEL_MIN_HEIGHT);

		appletContainer.init();
		if (loader.getBridge().getTransformSpec() == null) {
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					for (; ; ) {
						log.warning("Downloading update -- please wait");
						try {
							loader.upload(loader.getPackHash());
							break;
						} catch (IOException ignored) {
						} catch (NRSLoader.PendingException p) {
							//TODO: change to something more informative
							int d = p.getDelay() / 1000;
							log.warning("Request pending, trying again in " + (d < 60 ? d + " seconds" : (int) Math.ceil(d / 60) + " minutes"));
							try {
								Thread.sleep(p.getDelay());
							} catch (final InterruptedException ignored) {
								break;
							}
						}
					}
				}
			});
			thread.setDaemon(false);
			thread.setPriority(Thread.MAX_PRIORITY);
			thread.start();
			return;
		}
		setClient((Client) loader.getClient(), loader.getBridge().getTransformSpec());
		appletContainer.start();

		final Thread t = new Thread(threadGroup, new Runnable() {
			@Override
			public void run() {
				for (; ; ) {
					int s;
					if ((s = getMethodContext().game.getClientState()) >= Game.INDEX_LOGIN_SCREEN) {
						if (s == Game.INDEX_LOGIN_SCREEN) {
							getMethodContext().keyboard.send("{VK_ESCAPE}");
						}
						break;
					} else {
						try {
							Thread.sleep(300);
						} catch (final InterruptedException ignored) {
						}
					}
				}
			}
		});
		t.setPriority(Thread.MIN_PRIORITY);
		t.start();

		BotChrome.getInstance().panel.setBot(this);
	}

	@Override
	public boolean isStopping() {
		return stopping;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop() {
		stopping = true;
		log.info("Unloading environment");
		for (final Stoppable module : new Stoppable[]{mouseHandler, controller, multicaster}) {
			if (module != null) {
				module.stop();
			}
		}
		new Thread(threadGroup, new Runnable() {
			@Override
			public void run() {
				terminateApplet();
			}
		}).start();
	}

	void terminateApplet() {
		if (appletContainer != null) {
			log.fine("Shutting down applet");
			appletContainer.stop();
			appletContainer.destroy();
			appletContainer = null;
			this.ctx.setClient(null);
		}
	}

	public synchronized void startScript(final Script script, final ScriptDefinition def, final int timeout) {
		controller = new ScriptController(ctx, multicaster, script, def, timeout);
		controller.run();
	}

	public synchronized void stopScript() {
		if (controller != null) {
			controller.stop();
		}
		controller = null;
	}

	public BufferedImage getImage() {
		return image;
	}

	public BufferedImage getBuffer() {
		return backBuffer;
	}

	public Applet getAppletContainer() {
		return appletContainer;
	}

	public MethodContext getMethodContext() {
		return ctx;
	}

	public Constants getConstants() {
		return constants;
	}

	public InputHandler getInputHandler() {
		return inputHandler;
	}

	public MouseHandler getMouseHandler() {
		return mouseHandler;
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
		if (this.ctx.getClient() != null && panel != null && !BotChrome.getInstance().isMinimised()) {
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

	private void setClient(final Client client, TransformSpec spec) {
		this.ctx.setClient(client);
		client.setCallback(new AbstractCallback(this));
		constants = new Constants(spec.constants);
		new Thread(threadGroup, new SafeMode(this)).start();
		mouseHandler = new MouseHandler(appletContainer, client);
		inputHandler = new InputHandler(appletContainer, client);
		new Thread(threadGroup, mouseHandler).start();
	}

	public Canvas getCanvas() {
		final Client client = ctx.getClient();
		return client != null ? client.getCanvas() : null;
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
		return controller;
	}

	private final class SafeMode implements Runnable {
		private final Bot bot;

		public SafeMode(final Bot bot) {
			this.bot = bot;
		}

		public void run() {
			if (bot != null && bot.ctx.getClient() != null) {
				for (int i = 0; i < 30; i++) {
					if (!ctx.keyboard.isReady()) {
						try {
							Thread.sleep(Random.nextInt(500, 1000));
						} catch (InterruptedException ignored) {
						}
					} else {
						break;
					}
				}
				if (ctx.keyboard.isReady()) {
					ctx.keyboard.send("s");
				}
			}
		}
	}
}
