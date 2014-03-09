package org.powerbot.bot.os;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Map;

import javax.swing.JFrame;

import org.powerbot.bot.os.client.Client;
import org.powerbot.bot.os.event.EventDispatcher;
import org.powerbot.bot.os.loader.GameAppletLoader;
import org.powerbot.bot.os.loader.GameCrawler;
import org.powerbot.bot.os.loader.GameLoader;
import org.powerbot.bot.os.loader.GameStub;
import org.powerbot.gui.BotChrome;
import org.powerbot.script.PaintListener;
import org.powerbot.script.os.ClientContext;
import org.powerbot.util.Ini;

public class Bot extends org.powerbot.script.Bot {
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
		final GameCrawler crawler = new GameCrawler();
		if (!crawler.call()) {
			return;
		}

		final GameLoader game = new GameLoader(crawler);
		final ClassLoader classLoader = game.call();
		if (classLoader == null) {
			return;
		}

		if (crawler.properties.containsKey("title")) {
			chrome.setTitle(crawler.properties.get("title"));
		}

		final GameAppletLoader loader = new GameAppletLoader(game, classLoader);
		Thread.currentThread().setContextClassLoader(classLoader);
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

		final Ini.Member p = new Ini().put(crawler.properties).get();
		applet.setSize(new Dimension(p.getInt("width", 765), p.getInt("height", 503)));
		applet.setMinimumSize(applet.getSize());

		applet.start();
		debug();
		display();
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

	private void debug() {
		ctx.menu.register();
		new Thread(threadGroup, dispatcher, dispatcher.getClass().getName()).start();
		ctx.client(client);
		dispatcher.add(new PaintListener() {
			@Override
			public void repaint(final Graphics render) {
				//TODO: ??
			}
		});
	}
}
