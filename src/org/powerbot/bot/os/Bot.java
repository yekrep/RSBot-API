package org.powerbot.bot.os;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Map;

import javax.swing.SwingUtilities;

import org.powerbot.script.os.ClientContext;
import org.powerbot.bot.os.event.EventDispatcher;
import org.powerbot.bot.os.event.PaintListener;
import org.powerbot.bot.os.loader.GameAppletLoader;
import org.powerbot.bot.os.loader.GameCrawler;
import org.powerbot.bot.os.loader.GameLoader;
import org.powerbot.bot.os.loader.GameStub;
import org.powerbot.bot.os.client.Client;
import org.powerbot.gui.BotChrome;

public class Bot extends org.powerbot.script.Bot {
	public final ClientContext ctx;
	private Client client;

	public Bot(final BotChrome chrome) {
		super(chrome, new EventDispatcher());
		ctx = ClientContext.newContext(this);
	}

	@Override
	public void run() {
		final GameCrawler crawler = new GameCrawler();
		if (!crawler.call()) {
			return;
		}

		final GameLoader game = new GameLoader(crawler);
		final ClassLoader classLoader = game.call();
		if (classLoader == null) {
			return;
		}

		final GameAppletLoader loader = new GameAppletLoader(game, classLoader);
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

	private void hook(final GameAppletLoader loader) {
		applet = loader.getApplet();
		client = (Client) loader.getClient();
		final GameCrawler crawler = loader.getGameLoader().crawler;
		final GameStub stub = new GameStub(crawler.parameters, crawler.archive);
		applet.setStub(stub);
		applet.init();

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Dimension d = null;
				final Map<String, String> p = crawler.properties;
				if (p.containsKey("width") && p.containsKey("height")) {
					try {
						d = new Dimension(Integer.parseInt(p.get("width")), Integer.parseInt(p.get("height")));
					} catch (final NumberFormatException ignored) {
					}
				}
				d = d == null ? chrome.getSize() : d;

				applet.setSize(d);
				applet.setMinimumSize(d);
				chrome.add(applet);
				applet.start();
			}
		});

		debug();
	}

	private void debug() {
		ctx.menu.register();
		new Thread(threadGroup, dispatcher, dispatcher.getClass().getName()).start();
		ctx.setClient(client);
		dispatcher.add(new PaintListener() {
			@Override
			public void repaint(final Graphics render) {

			}
		});
	}

	@Override
	public void close() {
		if (applet != null) {
			applet.stop();
			applet.destroy();
		}
		threadGroup.interrupt();
	}
}
