package org.powerbot.os.client;

import java.applet.Applet;
import java.awt.Dimension;
import java.io.Closeable;
import java.io.IOException;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.powerbot.os.loader.Crawler;
import org.powerbot.os.loader.GameLoader;
import org.powerbot.os.loader.GameStub;
import org.powerbot.os.loader.OSRSLoader;
import org.powerbot.os.ui.BotChrome;

/**
 * @author Paris
 */
public class Bot implements Runnable, Closeable {
	private final BotChrome chrome;
	private final ThreadGroup group;
	private Applet applet;
	private Client client;

	public Bot(final BotChrome chrome) {
		this.chrome = chrome;
		group = new ThreadGroup(getClass().getSimpleName());
	}

	@Override
	public void run() {
		final Crawler crawler = new Crawler();
		if (!crawler.crawl()) {
			return;
		}

		final GameLoader game = new GameLoader(crawler);
		final ClassLoader classLoader = game.call();
		if (classLoader == null) {
			return;
		}

		final OSRSLoader loader = new OSRSLoader(game, classLoader);
		loader.setCallback(new Runnable() {
			@Override
			public void run() {
				hook(loader);
			}
		});
		final Thread t = new Thread(group, loader);
		t.setContextClassLoader(classLoader);
		t.start();
	}

	private void hook(final OSRSLoader loader) {
		final Dimension d = new Dimension(765, 503);
		applet = loader.getApplet();
		//TODO: client = (Client) loader.getClient();
		final Crawler crawler = loader.getGameLoader().getCrawler();
		final GameStub stub = new GameStub(crawler.parameters, crawler.archive);
		applet.setStub(stub);
		applet.init();

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				applet.setSize(d);
				applet.setMinimumSize(d);
				chrome.add(applet);
				applet.start();
			}
		});
	}

	@Override
	public void close() {
		if (applet != null) {
			applet.stop();
			applet.destroy();
		}
		group.interrupt();
	}
}
