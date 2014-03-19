package org.powerbot.bot.rt4;

import java.applet.Applet;
import java.awt.Dimension;
import java.awt.Point;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

import javax.swing.JFrame;

import org.powerbot.Configuration;
import org.powerbot.bot.loader.GameAppletLoader;
import org.powerbot.bot.loader.GameLoader;
import org.powerbot.bot.loader.GameStub;
import org.powerbot.bot.loader.Transformer;
import org.powerbot.bot.loader.transform.TransformSpec;
import org.powerbot.bot.rt4.client.Client;
import org.powerbot.bot.rt4.event.EventDispatcher;
import org.powerbot.gui.BotChrome;
import org.powerbot.misc.CryptFile;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.util.HttpUtils;
import org.powerbot.util.Ini;

public class Bot extends org.powerbot.script.Bot {
	private static final Logger log = Logger.getLogger(Bot.class.getName());
	public final ClientContext ctx;
	private Client client;

	public Bot(final BotChrome chrome) {
		super(chrome, new EventDispatcher());
		ctx = ClientContext.newContext(this);
	}

	@Override
	public ClientContext ctx() {
		return ctx;
	}

	@Override
	public void run() {
		log.info("Loading bot");
		final GameCrawler crawler = new GameCrawler();
		if (!crawler.call()) {
			log.severe("Failed to crawl game");
			return;
		}

		final GameLoader game = new GameLoader(crawler.archive, crawler.game) {
			@Override
			protected Transformer transformer() {
				final TransformSpec spec;
				try {
					final CryptFile cache = new CryptFile("rt4.ts", getClass());
					spec = new TransformSpec(cache.download(HttpUtils.getHttpConnection(new URL(String.format(Configuration.URLs.TSPEC, "4", hash)))));
				} catch (final IOException e) {
					throw new IllegalStateException("bad resource", e);
				}
				spec.adapt();
				return spec;
			}
		};
		final ClassLoader loader = game.call();
		if (loader == null) {
			log.severe("Failed to load game");
			return;
		}
		if (crawler.properties.containsKey("title")) {
			chrome.setTitle(crawler.properties.get("title"));
		}
		final GameAppletLoader bootstrap = new GameAppletLoader(loader, crawler.clazz) {
			@Override
			protected void sequence(final Applet applet) {
				log.info("Loading game");
				Bot.this.applet = applet;
				client = (Client) applet;
				final GameStub stub = new GameStub(crawler.parameters, crawler.archive);
				applet.setStub(stub);
				applet.init();

				final Ini.Member p = new Ini().put(crawler.properties).get();
				applet.setSize(new Dimension(p.getInt("width", 765), p.getInt("height", 503)));
				applet.setMinimumSize(applet.getSize());

				applet.start();
				initialize();
				display();
			}
		};
		Thread.currentThread().setContextClassLoader(loader);
		final Thread t = new Thread(threadGroup, bootstrap);
		t.setContextClassLoader(loader);
		t.start();
	}

	@Override
	public void display() {
		final Dimension d = chrome.getSize();
		super.display();

		final int s = chrome.getExtendedState(), x = s & ~JFrame.MAXIMIZED_BOTH;
		if (s != x) {
			chrome.setExtendedState(x);
			chrome.setLocationRelativeTo(chrome.getParent());
		} else {
			final Dimension dy = chrome.getSize();
			final Point p = chrome.getLocation();
			p.translate(d.width - dy.width, d.height - dy.height);
			chrome.setLocation(p);
		}

		chrome.setResizable(false);
	}

	private void initialize() {
		ctx.menu.register();
		new Thread(threadGroup, dispatcher, dispatcher.getClass().getName()).start();
		ctx.client(client);
	}
}
