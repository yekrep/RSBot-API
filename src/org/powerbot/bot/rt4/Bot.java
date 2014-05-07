package org.powerbot.bot.rt4;

import java.applet.Applet;
import java.awt.Dimension;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.powerbot.Configuration;
import org.powerbot.bot.loader.GameAppletLoader;
import org.powerbot.bot.loader.GameLoader;
import org.powerbot.bot.loader.GameStub;
import org.powerbot.bot.loader.LoaderUtils;
import org.powerbot.bot.loader.TransformSpec;
import org.powerbot.bot.loader.Transformer;
import org.powerbot.bot.rt4.activation.EventDispatcher;
import org.powerbot.bot.rt4.client.Client;
import org.powerbot.gui.BotChrome;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.util.Ini;

public class Bot extends org.powerbot.script.Bot<ClientContext> {
	public Bot(final BotChrome chrome) {
		super(chrome, new EventDispatcher());
	}

	@Override
	protected ClientContext newContext() {
		return ClientContext.newContext(this);
	}

	@Override
	public void run() {
		clearPreferences();
		log.info("Loading bot");
		final GameCrawler crawler = new GameCrawler();
		if (!crawler.call()) {
			log.severe("Failed to crawl game");
			return;
		}

		final GameLoader game = new GameLoader(crawler.archive, crawler.game) {
			@Override
			protected Transformer transformer() {
				TransformSpec spec;
				try {
					spec = LoaderUtils.get(ctx.rtv(), hash);
					spec.adapt();
				} catch (final IOException e) {
					if (!(e.getCause() instanceof IllegalStateException)) {
						log.severe("Failed to load transform specification");
						throw new IllegalStateException();
					}
					spec = null;
				}

				if (spec == null) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							LoaderUtils.submit(log, ctx.rtv(), hash, classes);
						}
					}).start();

					throw new IllegalStateException();
				}
				return spec;
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
		if (crawler.properties.containsKey("title")) {
			chrome.setTitle(crawler.properties.get("title"));
		}
		final GameAppletLoader bootstrap = new GameAppletLoader(loader, crawler.clazz) {
			@Override
			protected void load(final Applet applet) {
				log.info("Loading game");
				Bot.this.applet = applet;
				ctx.client((Client) applet);
				ctx.client().setCallback(new AbstractCallback(Bot.this));
				final GameStub stub = new GameStub(crawler.parameters, crawler.archive);
				applet.setStub(stub);
				applet.init();

				final Ini.Member p = new Ini().put(crawler.properties).get();
				final Dimension d = new Dimension(p.getInt("width", 765), p.getInt("height", 503));
				applet.setSize(d);
				applet.setPreferredSize(d);
				applet.setMinimumSize(d);

				applet.start();
				initialize();
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						display();
					}
				});
			}
		};
		Thread.currentThread().setContextClassLoader(loader);
		bootstrap.getLoaderThread(threadGroup).start();
	}

	@Override
	public void display() {
		super.display();

		final int s = chrome.getExtendedState(), x = s & ~JFrame.MAXIMIZED_BOTH;
		if (s != x) {
			chrome.setExtendedState(x);
			chrome.setLocationRelativeTo(chrome.getParent());
		}

		if (Configuration.OS != Configuration.OperatingSystem.WINDOWS) {
			chrome.setResizable(false);
		}
	}

	private void initialize() {
		ctx.menu.register();
		new Thread(threadGroup, dispatcher, dispatcher.getClass().getName()).start();
	}
}
