package org.powerbot.bot.rt6;

import java.applet.Applet;
import java.awt.Dimension;
import java.awt.Window;
import java.io.IOException;
import java.util.TimerTask;
import java.util.concurrent.Callable;

import javax.swing.SwingUtilities;

import org.powerbot.bot.loader.GameAppletLoader;
import org.powerbot.bot.loader.GameCrawler;
import org.powerbot.bot.loader.GameLoader;
import org.powerbot.bot.loader.GameStub;
import org.powerbot.bot.loader.LoaderUtils;
import org.powerbot.bot.loader.TransformSpec;
import org.powerbot.bot.loader.Transformer;
import org.powerbot.bot.rt6.client.Client;
import org.powerbot.bot.rt6.client.Constants;
import org.powerbot.bot.rt6.loader.AbstractBridge;
import org.powerbot.bot.rt6.loader.AbstractProcessor;
import org.powerbot.bot.rt6.loader.AppletTransform;
import org.powerbot.bot.rt6.loader.Application;
import org.powerbot.bot.rt6.loader.ClassLoaderTransform;
import org.powerbot.bot.rt6.loader.ListClassesTransform;
import org.powerbot.gui.BotChrome;
import org.powerbot.misc.GoogleAnalytics;
import org.powerbot.script.Condition;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.Component;
import org.powerbot.script.rt6.Game;
import org.powerbot.util.Ini;

public final class Bot extends org.powerbot.script.Bot<ClientContext> {

	public Bot(final BotChrome chrome) {
		super(chrome, new EventDispatcher());
	}

	@Override
	protected ClientContext newContext() {
		return ClientContext.newContext(this);
	}

	@Override
	public void run() {
		log.info("Loading bot");

		final String k = "game.safemode";
		if (Ini.parseBoolean(System.getProperty(k))) {
			((Window) chrome.overlay.getAndSet(null)).dispose();
		}
		System.clearProperty(k);

		final GameCrawler gameCrawler = GameCrawler.download("www");
		if (gameCrawler == null) {
			log.severe("Failed to crawl game");
			return;
		}
		final AppletTransform appletTransform = new AppletTransform();
		final GameLoader game = new GameLoader(gameCrawler.archive, gameCrawler.game) {
			@Override
			protected Transformer transformer() {
				return new AbstractProcessor(appletTransform,
						new ClassLoaderTransform(appletTransform), new ListClassesTransform(appletTransform)
				);
			}
		};

		final ClassLoader loader;
		try {
			loader = game.call();
		} catch (final Exception ignored) {
			log.severe("Failed to load game");
			return;
		}
		if (loader == null) {
			log.severe("Failed to load game");
			return;
		}
		if (gameCrawler.properties.containsKey("title")) {
			chrome.setTitle(gameCrawler.properties.get("title"));
		}
		final GameAppletLoader bootstrap = new GameAppletLoader(loader, gameCrawler.clazz) {
			@Override
			protected void load(final Applet applet) {
				Bot.this.sequence(game, gameCrawler, applet);
			}
		};
		bootstrap.getLoaderThread().start();

		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (ctx.game == null) {
					return;
				}
				final int s = ctx.game.clientState();
				if (s == Game.INDEX_LOGIN_SCREEN || s == Game.INDEX_LOGGING_IN) {
					final Component e = ctx.widgets.component(Login.WIDGET, Login.WIDGET_LOGIN_ERROR);
					if (e.visible()) {
						String m = null;
						final String txt = e.text().toLowerCase();

						if (txt.contains("your ban will be lifted in")) {
							m = "ban";
						} else if (txt.contains("account has been disabled")) {
							m = "disabled";
						}

						if (m != null) {
							GoogleAnalytics.getInstance().pageview("scripts/0/login/" + m, txt);
						}
					}
				}
			}
		}, 6000, 3000);
	}

	private void sequence(final GameLoader game, final GameCrawler gameCrawler, final Applet applet) {
		final byte[] inner = game.resource("inner.pack.gz");
		final String h;
		if (inner == null || (h = LoaderUtils.hash(inner)) == null) {
			return;
		}
		TransformSpec spec;
		try {
			spec = LoaderUtils.get(ctx.rtv(), h);
		} catch (final IOException e) {
			if (!(e.getCause() instanceof IllegalStateException)) {
				log.severe("Failed to load transform specification");
				return;
			}
			spec = null;
		}

		final TransformSpec spec_ = spec;
		final AbstractBridge bridge = new AbstractBridge(spec) {
			@Override
			public void instance(final Object client) {
				if (spec_ != null) {
					ctx.client((Client) client);
				}
			}
		};
		((Application) applet).setBridge(bridge);

		this.applet = applet;
		final GameStub stub = new GameStub(gameCrawler.parameters, gameCrawler.archive);
		applet.setStub(stub);
		applet.setSize(BotChrome.PANEL_MIN_WIDTH, BotChrome.PANEL_MIN_HEIGHT);
		applet.setMinimumSize(new Dimension(BotChrome.PANEL_MIN_WIDTH, BotChrome.PANEL_MIN_HEIGHT));
		applet.init();

		if (spec == null) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					LoaderUtils.submit(log, ctx.rtv(), h, bridge.loaded);
				}
			}).start();
			return;
		}

		ctx.client().setCallback(new AbstractCallback(this));
		ctx.constants.set(new Constants(spec.constants));
		applet.start();
		new Thread(dispatcher, dispatcher.getClass().getName()).start();

		if (chrome.overlay.get() == null) {
			new Thread(new SafeMode()).start();
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				display();
			}
		});
	}

	private final class SafeMode implements Runnable {
		@Override
		public void run() {
			if (Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return ctx.client().getCanvas() != null;
				}
			})) {
				ctx.input.send("s");
			}
		}
	}
}
