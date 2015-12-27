package org.powerbot.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

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
	public final AtomicReference<Window> ad;
	public final Ini config;

	public BotChrome() {
		bot = new AtomicReference<AbstractBot>(null);
		window = new AtomicReference<Frame>(null);
		menu = new AtomicReference<BotMenuBar>(null);
		target = new AtomicReference<Component>(null);
		overlay = new AtomicReference<BotOverlay>(null);
		ad = new AtomicReference<Window>(null);
		config = new Ini();
	}

	public static BotChrome getInstance() {
		return instance;
	}

	@Override
	public void run() {
		if (!window.compareAndSet(null, null)) {
			return;
		}

		final boolean rt4 = System.getProperty("com.jagex.config", "").startsWith("http://oldschool.");
		bot.set(rt4 ? new org.powerbot.bot.rt4.Bot(BotChrome.this) : new org.powerbot.bot.rt6.Bot(BotChrome.this));

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

		final AtomicReference<BufferedImage> icon = new AtomicReference<BufferedImage>();
		new Thread(new Runnable() {
			@Override
			public void run() {
				final String f = Boot.properties.getProperty("icon");
				if (f != null) {
					final File ico = new File(f);
					try {
						HttpUtils.download(new URL(Configuration.URLs.ICON), ico);
						icon.set(ImageIO.read(ico));
					} catch (final IOException ignored) {
					}
				}
			}
		}).start();

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

							final BotOverlay o = new BotOverlay(BotChrome.this);
							if (o.supported) {
								overlay.set(o);
							} else {
								o.dispose();
							}

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

				new Thread(new Runnable() {
					@Override
					public void run() {
						isLatestVersion();
						new AdPanel(BotChrome.this).run();
					}
				}).start();

				JPopupMenu.setDefaultLightWeightPopupEnabled(false);

				if (icon.get() != null) {
					f.setIconImage(icon.get());
				}

				final WindowListener[] listeners = f.getWindowListeners();
				for (final WindowListener l : listeners) {
					f.removeWindowListener(l);
				}
				f.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(final WindowEvent e) {
						log.info("Shutting down");

						if (bot.get() != null) {
							final Frame w = window.get();
							if (w != null && ((w.getExtendedState() ^ Frame.MAXIMIZED_BOTH) != 0)) {
								final CryptFile c = new CryptFile("window.1.ini");
								try {
									new Ini().read(c.getInputStream()).get(rt4 ? "rt4" : "rt6").
											put("w", w.getWidth()).put("h", w.getHeight()).parent().
											write(c.getOutputStream());
								} catch (final IOException ignored) {
								}
							}

							bot.get().close();
						}

						if (overlay.get() != null) {
							overlay.getAndSet(null).dispose();
						}
					}
				});
				for (final WindowListener l : listeners) {
					f.addWindowListener(l);
				}

				if (menu.get() == null) {
					menu.set(new BotMenuBar(BotChrome.this));
				}
				f.setMenuBar(menu.get());

				f.setSize(f.getMinimumSize());
				f.setLocationRelativeTo(f.getParent());

				if (Configuration.OS == Configuration.OperatingSystem.MAC) {
					new OSXAdapt(BotChrome.this).run();
				}

				new Thread(new CliController(BotChrome.this)).start();
			}
		});
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
			final String msg = String.format("A newer version is available, please download from %s", BotLocale.WEBSITE);
			log.log(Level.SEVERE, msg);
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(window.get(), msg, "Update", JOptionPane.PLAIN_MESSAGE);
					window.get().dispatchEvent(new WindowEvent(window.get(), WindowEvent.WINDOW_CLOSING));
				}
			});
			return false;
		}

		if (Configuration.OS == Configuration.OperatingSystem.WINDOWS &&
				(Configuration.JRE6 || System.getProperty("sun.arch.data.model", "").equals("64"))) {
			final String msg = "Please update to Java 8 (or newer) 32-bit/x86";
			log.log(Level.SEVERE, msg);
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					final int r = JOptionPane.showConfirmDialog(window.get(), msg, "Java Requirement", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
					if (r == JOptionPane.OK_OPTION) {
						window.get().dispatchEvent(new WindowEvent(window.get(), WindowEvent.WINDOW_CLOSING));
					}
				}
			});
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
		if (window.get() != null) {
			final Window f = window.getAndSet(null);
			f.dispatchEvent(new WindowEvent(f, WindowEvent.WINDOW_CLOSING));
		}
	}
}
