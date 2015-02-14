package org.powerbot.bot.loader;

import java.applet.Applet;
import java.lang.reflect.Constructor;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

public abstract class GameAppletLoader implements Callable<Void> {
	private final ClassLoader loader;
	private final String codesource;

	public GameAppletLoader(final ClassLoader loader, final String codesource) {
		this.loader = loader;
		this.codesource = codesource;
	}

	@Override
	public Void call() throws Exception {
		final Class<?> code = loader.loadClass(codesource);
		if (!(Applet.class.isAssignableFrom(code))) {
			throw new IllegalArgumentException();
		}
		final Constructor<?> ctor = code.getConstructor((Class[]) null);
		final Applet applet = (Applet) ctor.newInstance((Object[]) null);
		load(applet);
		return null;
	}

	public Thread getLoaderThread() {
		return new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					call();
				} catch (final Exception e) {
					Logger.getLogger(getClass().getName()).severe("Failed to load game (loader thread)");
					e.printStackTrace();
				}
			}
		});
	}

	protected abstract void load(final Applet applet);
}
