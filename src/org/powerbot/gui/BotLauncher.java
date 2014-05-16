package org.powerbot.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.powerbot.Configuration;
import org.powerbot.misc.CryptFile;
import org.powerbot.misc.Resources;
import org.powerbot.script.Bot;
import org.powerbot.util.HttpUtils;
import org.powerbot.util.IOUtils;

public class BotLauncher implements Callable<Boolean>, Closeable {
	private static final Logger log = Logger.getLogger(BotLauncher.class.getName());
	private static final BotLauncher instance = new BotLauncher();
	public final AtomicReference<Bot> bot;
	public final AtomicReference<Frame> window;
	public final AtomicReference<BotMenuBar> menu;
	public final AtomicReference<BotOverlay> overlay;

	public BotLauncher() {
		bot = new AtomicReference<Bot>(null);
		window = new AtomicReference<Frame>(null);
		menu = new AtomicReference<BotMenuBar>(null);
		overlay = new AtomicReference<BotOverlay>(null);
	}

	public static BotLauncher getInstance() {
		return instance;
	}

	@Override
	public Boolean call() throws Exception {
		String mode = System.getProperty(Configuration.URLs.GAME_VERSION_KEY, "").toLowerCase();
		mode = mode.equals("oldschool") || mode.equals("os") ? "oldschool" : "www";
		System.clearProperty(Configuration.URLs.GAME_VERSION_KEY);
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
							} else {
								f.setMinimumSize(x.getSize());
							}
						}

						JPopupMenu.setDefaultLightWeightPopupEnabled(false);
						f.setIconImage(Resources.getImage(Resources.Paths.ICON));
						f.addWindowListener(new WindowAdapter() {
							@Override
							public void windowClosing(final WindowEvent e) {
								close();
							}
						});

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

		if (Configuration.OS == Configuration.OperatingSystem.MAC) {
			new OSXAdapt(this).run();
		}

		final Object o = Class.forName(name.substring(0, name.indexOf('.'))).newInstance();
		o.getClass().getMethod("main", new Class[]{String[].class}).invoke(o, new Object[]{new String[]{""}});
		return true;
	}

	private boolean isLatestVersion() {
		final CryptFile cache = new CryptFile("version.1.txt", getClass());
		final int version;
		try {
			version = Integer.parseInt(IOUtils.readString(cache.download(new URL(Configuration.URLs.VERSION))).trim());
		} catch (final Exception e) {
			String msg = "Error reading server data";
			if (SocketException.class.isAssignableFrom(e.getClass()) || SocketTimeoutException.class.isAssignableFrom(e.getClass())) {
				msg = "Could not connect to " + Configuration.URLs.DOMAIN + " server";
			}
			log.log(Level.SEVERE, msg, BotLocale.ERROR);
			return false;
		}
		if (version > Configuration.VERSION) {
			log.log(Level.SEVERE, String.format("A newer version is available, please download from %s", BotLocale.WEBSITE), "Update");
			return false;
		}
		log.info("Welcome to " + Configuration.NAME + ", please select your game version\r\n" +
				"To play a script click " + BotLocale.EDIT + " > " + BotLocale.SCRIPT_PLAY +
				(Configuration.OS == Configuration.OperatingSystem.MAC ? " (\u2318,)" : ""));
		return true;
	}

	public static void openURL(final String url) {
		if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
			return;
		}
		final URI uri;
		try {
			uri = new URI(url);
		} catch (final URISyntaxException ignored) {
			return;
		}
		try {
			Desktop.getDesktop().browse(uri);
		} catch (final IOException ignored) {
		}
	}

	@Override
	public void close() {
		log.info("Shutting down");

		boolean pending = false;
		if (bot.get() != null) {
			pending = bot.get().pending.get();
			new Thread(new Runnable() {
				@Override
				public void run() {
					bot.get().close();
				}
			}).start();
		}

		if (overlay.get() != null) {
			overlay.getAndSet(null).dispose();
		}
		window.get().dispose();

		if (Configuration.OS == Configuration.OperatingSystem.WINDOWS) {
			System.exit(0);
			return;
		}

		final long timeout = TimeUnit.SECONDS.toMillis(pending ? 120 : 6);
		final Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(timeout);
					log.info("Terminating process");
					System.exit(1);
				} catch (final InterruptedException ignored) {
				}
			}
		});
		t.setDaemon(true);
		t.setPriority(Thread.MIN_PRIORITY);
		t.start();
	}
}
