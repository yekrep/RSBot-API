package org.powerbot.bot.rs3;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import org.powerbot.Configuration;
import org.powerbot.bot.rs3.loader.Crawler;
import org.powerbot.bot.rs3.loader.GameLoader;
import org.powerbot.bot.rs3.loader.GameStub;
import org.powerbot.bot.rs3.loader.NRSLoader;
import org.powerbot.bot.rs3.loader.transform.TransformSpec;
import org.powerbot.bot.rs3.client.Client;
import org.powerbot.bot.rs3.client.Constants;
import org.powerbot.bot.rs3.event.EventDispatcher;
import org.powerbot.bot.KeyboardSimulator;
import org.powerbot.gui.BotChrome;
import org.powerbot.script.rs3.ClientContext;
import org.powerbot.script.Condition;

public final class Bot extends org.powerbot.script.Bot {
	private static final Logger log = Logger.getLogger(Bot.class.getName());
	private final BotChrome chrome;
	public final ClientContext ctx;

	public Bot(final BotChrome chrome) {
		super(chrome, new EventDispatcher());
		this.chrome = chrome;
		ctx = ClientContext.newContext(this);
	}

	@Override
	public ClientContext ctx() {
		return ctx;
	}

	@Override
	public void run() {
		log.info("Loading bot");
		final Crawler crawler = new Crawler();
		if (!crawler.crawl()) {
			log.severe("Failed to load game");
			return;
		}

		final GameLoader game = new GameLoader(crawler);
		final ClassLoader classLoader = game.call();
		if (classLoader == null) {
			log.severe("Failed to start game");
			return;
		}

		if (crawler.details.containsKey("title")) {
			chrome.setTitle(crawler.details.get("title"));
		}

		final NRSLoader loader = new NRSLoader(game, classLoader);
		loader.setCallback(new Runnable() {
			@Override
			public void run() {
				hook(loader);
			}
		});
		final Thread t = new Thread(threadGroup, loader);
		t.setContextClassLoader(classLoader);
		t.start();
	}

	private void hook(final NRSLoader loader) {
		if (Thread.interrupted()) {
			return;
		}

		log.info("Loading game (" + loader.getPackHash().substring(0, 6) + ")");

		applet = loader.getApplet();
		final Crawler crawler = loader.getGameLoader().getCrawler();
		final GameStub stub = new GameStub(crawler.parameters, crawler.archive);
		applet.setStub(stub);
		applet.setSize(BotChrome.PANEL_MIN_WIDTH, BotChrome.PANEL_MIN_HEIGHT);
		applet.init();

		if (loader.getBridge().getTransformSpec() == null) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					for (;;) {
						log.warning("Downloading update \u2014 please wait");
						pending.set(true);
						try {
							loader.upload(loader.getPackHash());
							break;
						} catch (final IOException ignored) {
						} catch (final NRSLoader.PendingException p) {
							final int d = p.getDelay() / 1000;
							log.warning("Your update is being processed, trying again in " + (d < 60 ? d + " seconds" : (int) Math.ceil(d / 60) + " minutes"));
							try {
								Thread.sleep(p.getDelay());
							} catch (final InterruptedException ignored) {
								break;
							}
						}
						pending.set(false);
					}
				}
			}).start();
			return;
		}

		setClient((Client) loader.getClient(), loader.getBridge().getTransformSpec());
		applet.start();
		new Thread(threadGroup, dispatcher, dispatcher.getClass().getName()).start();

		final boolean safemode;
		safemode = Configuration.OS == Configuration.OperatingSystem.MAC && !System.getProperty("java.version").startsWith("1.6");

		if (safemode) {
			new Thread(threadGroup, new SafeMode()).start();
		}

		chrome.display(this);
	}

	private void setClient(final Client client, final TransformSpec spec) {
		ctx.client(client);
		client.setCallback(new AbstractCallback(this));
		ctx.constants.set(new Constants(spec.constants));
		ctx.inputHandler.set(new KeyboardSimulator(applet, client));
	}

	private final class SafeMode implements Runnable {
		@Override
		public void run() {
			if (Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return ctx.client().getKeyboard() != null;
				}
			})) {
				ctx.keyboard.send("s");
			}
		}
	}
}
