package org.powerbot.os.bot;

import org.powerbot.os.api.MethodContext;
import org.powerbot.os.api.wrappers.Player;
import org.powerbot.os.api.wrappers.Tile;
import org.powerbot.os.bot.loader.GameAppletLoader;
import org.powerbot.os.bot.loader.GameCrawler;
import org.powerbot.os.bot.loader.GameLoader;
import org.powerbot.os.bot.loader.GameStub;
import org.powerbot.os.client.Client;
import org.powerbot.os.event.EventDispatcher;
import org.powerbot.os.event.PaintListener;
import org.powerbot.os.gui.BotChrome;

import javax.swing.*;
import java.applet.Applet;
import java.awt.*;
import java.io.Closeable;
import java.util.Map;

public class Bot implements Runnable, Closeable {
	private final BotChrome chrome;
	private final ThreadGroup group;
	public final EventDispatcher dispatcher;
	public final MethodContext ctx;
	private Applet applet;
	private Client client;

	public Bot(final BotChrome chrome) {
		this.chrome = chrome;
		group = new ThreadGroup(getClass().getSimpleName());
		dispatcher = new EventDispatcher();
		ctx = MethodContext.newContext(this);
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
		final Thread t = new Thread(group, loader);
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
				chrome.panel.setVisible(false);
				applet.start();
			}
		});

		debug();
	}

	private void debug() {
		new Thread(group, dispatcher, dispatcher.getClass().getName()).start();
		ctx.setClient(client);
		dispatcher.add(new PaintListener() {
			@Override
			public void repaint(final Graphics render) {
				final java.util.List<Player> players = ctx.players.getLoaded();
				for (final Player p : players) {
					final Tile t = p.getLocation();
				}
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
