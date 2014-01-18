package org.powerbot.os.loader;

import java.applet.Applet;
import java.lang.reflect.Constructor;

public class GameBotLoader implements Runnable {
	private final GameLoader gameLoader;
	private final ClassLoader classLoader;
	private Runnable callback;
	private Applet applet;
	private Object client;
	private String packHash;

	public GameBotLoader(final GameLoader gameLoader, final ClassLoader classLoader) {
		this.gameLoader = gameLoader;
		this.classLoader = classLoader;
	}

	public void setCallback(final Runnable callback) {
		this.callback = callback;
	}

	@Override
	public void run() {
		Class<?> code;
		try {
			code = classLoader.loadClass(gameLoader.crawler.clazz);
		} catch (final ClassNotFoundException e) {
			code = null;
		}
		if (code == null || !(Applet.class.isAssignableFrom(code))) {
			return;
		}
		try {
			final Constructor<?> constructor = code.getConstructor((Class[]) null);
			this.applet = (Applet) constructor.newInstance((Object[]) null);
		} catch (final Exception ignored) {
			this.applet = null;
		}
		if (applet == null) {
			return;
		}

		client = applet;
		callback.run();
	}

	public GameLoader getGameLoader() {
		return gameLoader;
	}

	public Applet getApplet() {
		return applet;
	}

	public Object getClient() {
		return client;
	}
}
