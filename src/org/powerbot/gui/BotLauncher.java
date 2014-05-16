package org.powerbot.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.io.Closeable;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.powerbot.Configuration;
import org.powerbot.script.Bot;
import org.powerbot.util.HttpUtils;

public class BotLauncher implements Callable<Boolean>, Closeable {
	private static final BotLauncher instance = new BotLauncher();
	public final AtomicReference<Bot> bot;
	public final AtomicReference<Frame> window;
	public final AtomicReference<BotMenuBar> menu;

	public BotLauncher() {
		bot = new AtomicReference<Bot>(null);
		window = new AtomicReference<Frame>(null);
		menu = new AtomicReference<BotMenuBar>(null);
	}

	public static BotLauncher getInstance() {
		return instance;
	}

	@Override
	public Boolean call() throws Exception {
		final String mode = "www"; // also oldschool or world200 for beta
		System.setProperty("com.jagex.config",
				String.format("http://%s.%s/k=3/l=%s/jav_config.ws", mode, Configuration.URLs.GAME, System.getProperty("user.language", "en")));

		final URL src = new URL("http://www." + Configuration.URLs.GAME + "/downloads/jagexappletviewer.jar");
		final String name = src.getFile().substring(src.getFile().lastIndexOf('/') + 1);
		final File jar = new File(Configuration.HOME, name);
		if (!jar.exists()) {
			HttpUtils.download(src, jar);
		}

		final Method m = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
		m.setAccessible(true);
		m.invoke(ClassLoader.getSystemClassLoader(), jar.toURI().toURL());

		new Thread(new Runnable() {
			@Override
			public void run() {
				window.set(null);
				String t = Configuration.URLs.GAME;
				t = t.substring(0, t.indexOf('.')).toLowerCase();

				for (; ; ) {
					try {
						Thread.sleep(60);
					} catch (final InterruptedException ignored) {
						break;
					}
					final Frame[] f = Frame.getFrames();
					if (f == null || f.length == 0) {
						continue;
					}
					for (final Frame x : f) {
						final String s = x.getTitle();
						if (s != null && s.toLowerCase().endsWith(t)) {
							window.set(x);
							break;
						}
					}
					if (window.get() != null) {
						break;
					}
				}

				if (window.get() == null) {
					return;
				}

				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						final Frame f = window.get();
						Component[] c = new Component[0];

						while (c.length == 0) {
							c = f.getComponents();
							try {
								Thread.sleep(60);
							} catch (final InterruptedException ignored) {
							}
						}

						if (c.length == 1 && c[0] instanceof Container) {
							c = ((Container) c[0]).getComponents();
						}

						for (final Component x : c) {
							final String s = x.getClass().getSimpleName();
							if (!(s.equals("Rs2Applet") || s.equals("client"))) {
								x.setVisible(false);
							}
						}

						JPopupMenu.setDefaultLightWeightPopupEnabled(false);
						f.setMinimumSize(new Dimension(800, 600));

						if (menu.get() == null) {
							menu.set(new BotMenuBar(BotLauncher.this));
						}
						f.setMenuBar(menu.get());

						f.setSize(f.getMinimumSize());
						f.setLocationRelativeTo(f.getParent());
					}
				});
			}
		}).start();

		final Object o = Class.forName(name.substring(0, name.indexOf('.'))).newInstance();
		o.getClass().getMethod("main", new Class[]{String[].class}).invoke(o, new Object[]{new String[]{""}});
		return true;
	}

	@Override
	public void close() {
	}
}
