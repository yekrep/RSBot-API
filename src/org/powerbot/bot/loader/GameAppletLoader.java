package org.powerbot.bot.loader;

import java.applet.Applet;
import java.lang.reflect.Constructor;

public abstract class GameAppletLoader implements Runnable {
	private final ClassLoader loader;
	private final String codesource;

	public GameAppletLoader(final ClassLoader loader, final String codesource) {
		this.loader = loader;
		this.codesource = codesource;
	}

	@Override
	public void run() {
		Class<?> code;
		try {
			code = loader.loadClass(codesource);
		} catch (final ClassNotFoundException e) {
			code = null;
		}
		if (code == null || !(Applet.class.isAssignableFrom(code))) {
			error();
			return;
		}
		final Applet applet;
		try {
			final Constructor<?> constructor = code.getConstructor((Class[]) null);
			applet = (Applet) constructor.newInstance((Object[]) null);
		} catch (final Exception ignored) {
			error();
			return;
		}
		sequence(applet);
	}

	protected abstract void sequence(final Applet applet);

	protected abstract void error();
}
