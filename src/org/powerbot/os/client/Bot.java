package org.powerbot.os.client;

import java.applet.Applet;
import java.awt.Dimension;
import java.util.logging.Logger;

import org.powerbot.os.loader.Crawler;
import org.powerbot.os.loader.GameLoader;
import org.powerbot.os.loader.GameStub;
import org.powerbot.os.loader.OSRSLoader;
import org.powerbot.os.ui.BotChrome;

/**
 * @author Paris
 */
public class Bot implements Runnable {
	private static final Logger log = Logger.getLogger(Bot.class.getSimpleName());
	private final BotChrome chrome;
	private Applet applet;
	private Client client;

	public Bot(final BotChrome chrome) {
		this.chrome = chrome;
	}

	@Override
	public void run() {
		log.info("Crawling ...");
		final Crawler crawler = new Crawler();
		if (!crawler.crawl()) {
			log.severe("Failed to load game");
			return;
		}

		log.info("Downloading game ...");
		final GameLoader game = new GameLoader(crawler);
		final ClassLoader classLoader = game.call();
		if (classLoader == null) {
			log.severe("Failed to start game");
			return;
		}

		log.info("Launching loader ...");
		final OSRSLoader loader = new OSRSLoader(game, classLoader);
		loader.setCallback(new Runnable() {
			@Override
			public void run() {
				hook(loader);
			}
		});
		final Thread t = new Thread(loader);
		t.setContextClassLoader(classLoader);
		t.start();
	}

	private void hook(final OSRSLoader loader) {
		log.info("Loading game");
		final Dimension d = new Dimension(756, 503);
		applet = loader.getApplet();
		//TODO: client = (Client) loader.getClient();
		final Crawler crawler = loader.getGameLoader().getCrawler();
		final GameStub stub = new GameStub(crawler.parameters, crawler.archive);
		applet.setStub(stub);
		applet.setSize(d);
		applet.setMinimumSize(d);
		chrome.add(applet);
		applet.init();
		applet.start();
	}
}
