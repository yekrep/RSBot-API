package org.powerbot.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Closeable;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JPopupMenu;

import org.powerbot.Boot;
import org.powerbot.Configuration;
import org.powerbot.bot.AbstractBot;
import org.powerbot.misc.CryptFile;
import org.powerbot.util.HttpUtils;
import org.powerbot.util.Ini;

public class BotChrome implements Runnable, Closeable {
	private static final Logger log = Logger.getLogger("Chrome");
	private static final BotChrome instance = new BotChrome();
	public final AtomicReference<AbstractBot> bot;
	public final AtomicReference<Frame> window;
	public final AtomicReference<BotMenuBar> menu;
	public final AtomicReference<Component> target;
	public final AtomicReference<BotOverlay> overlay;
	public final Ini config;

	public BotChrome() {
		bot = new AtomicReference<AbstractBot>(null);
		window = new AtomicReference<Frame>(null);
		menu = new AtomicReference<BotMenuBar>(null);
		target = new AtomicReference<Component>(null);
		overlay = new AtomicReference<BotOverlay>(null);
		config = new Ini();
	}

	public static BotChrome getInstance() {
		return instance;
	}

	@Override
	public void run() {
		window.set(null);
		String t = Configuration.URLs.GAME;
		t = t.substring(0, t.indexOf('.')).toLowerCase();

		do {
			for (final Frame x : Frame.getFrames()) {
				final String s = x.getTitle();
				if (s != null && s.toLowerCase().endsWith(t)) {
					window.set(x);
				}
			}
		} while (window.get() == null);

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				final Frame f = window.get();
				Component[] c;
				do {
					try {
						c = f.getComponents();
					} catch (final ArrayIndexOutOfBoundsException ignored) {
						c = new Component[0];
					}
				} while (c.length == 0);

				final Container p = c.length == 1 && c[0] instanceof Container ? (Container) c[0] : f;
				do {
					c = p.getComponents();

					for (final Component x : c) {
						if (x == null) {
							continue;
						}
						final String s = x.getClass().getSimpleName();
						if (s.equals("Rs2Applet") || s.equals("client")) {
							if (!target.compareAndSet(null, x)) {
								continue;
							}
							final boolean rt4 = System.getProperty("com.jagex.config", "").startsWith("http://oldschool.");

							final BotOverlay o = new BotOverlay(BotChrome.this);
							if (o.supported) {
								overlay.set(o);
							} else {
								o.dispose();
							}

							bot.set(rt4 ? new org.powerbot.bot.rt4.Bot(BotChrome.this) : new org.powerbot.bot.rt6.Bot(BotChrome.this));
							new Thread(bot.get()).start();
							final Dimension d = x.getSize();
							d.setSize(Math.min(800, x.getWidth()), Math.min(600, x.getHeight()));
							final Insets t = f.getInsets();
							d.setSize(d.getWidth() + t.right + t.left, d.getHeight() + t.top + t.bottom);
							f.setMinimumSize(d);
						} else {
							x.setVisible(false);
						}
					}

				} while (c.length < 3);

				isLatestVersion();
				JPopupMenu.setDefaultLightWeightPopupEnabled(false);

				if (Boot.icon != null) {
					try {
						HttpUtils.download(new URL(Configuration.URLs.ICON), Boot.icon);
						f.setIconImage(ImageIO.read(Boot.icon));
					} catch (final IOException ignored) {
					}
				}

				f.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(final WindowEvent e) {
						close();
					}
				});

				if (menu.get() == null) {
					menu.set(new BotMenuBar(BotChrome.this));
				}
				f.setMenuBar(menu.get());

				f.setSize(f.getMinimumSize());
				f.setLocationRelativeTo(f.getParent());

				if (Configuration.OS == Configuration.OperatingSystem.MAC) {
					new OSXAdapt(BotChrome.this).run();
				}
			}
		});
	}

	public void update() {
		if (!isLatestVersion()) {
			bot.set(null);
			return;
		}

		if (bot.get() instanceof org.powerbot.bot.rt6.Bot && overlay.get() == null) {
			new Thread(((org.powerbot.bot.rt6.Bot) bot.get()).new SafeMode()).start();
		}

		menu.get().update();
	}

	private boolean isLatestVersion() {
		final CryptFile cache = new CryptFile("control.1.ini");
		try {
			config.read(cache.download(new URL(Configuration.URLs.CONTROL)));
		} catch (final Exception e) {
			String msg = "Error reading server data";
			if (SocketException.class.isAssignableFrom(e.getClass()) || SocketTimeoutException.class.isAssignableFrom(e.getClass())) {
				msg = "Could not connect to " + Configuration.URLs.DOMAIN + " server";
			}
			log.log(Level.SEVERE, msg, BotLocale.ERROR);
			return false;
		}

		if (config.get().getInt("version") > Configuration.VERSION) {
			log.log(Level.SEVERE, String.format("A newer version is available, please download from %s", BotLocale.WEBSITE), "Update");
			return false;
		}

		log.info("Select your game, then to play a script click " + BotLocale.EDIT + " > " + BotLocale.SCRIPT_PLAY +
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
