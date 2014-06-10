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
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import org.powerbot.Boot;
import org.powerbot.Configuration;
import org.powerbot.misc.CryptFile;
import org.powerbot.script.Bot;
import org.powerbot.util.HttpUtils;
import org.powerbot.util.IOUtils;

public class BotLauncher implements Runnable, Closeable {
	private static final Logger log = Logger.getLogger("Launcher");
	private static final BotLauncher instance = new BotLauncher();
	public final AtomicReference<Bot> bot;
	public final AtomicReference<Frame> window;
	public final AtomicReference<BotMenuBar> menu;
	public final AtomicReference<Component> target;
	public final AtomicReference<BotOverlay> overlay;

	public BotLauncher() {
		bot = new AtomicReference<Bot>(null);
		window = new AtomicReference<Frame>(null);
		menu = new AtomicReference<BotMenuBar>(null);
		target = new AtomicReference<Component>(null);
		overlay = new AtomicReference<BotOverlay>(null);
	}

	public static BotLauncher getInstance() {
		return instance;
	}

	@Override
	public void run() {
		window.set(null);
		String t = Configuration.URLs.GAME;
		t = t.substring(0, t.indexOf('.')).toLowerCase();

		do {
			Thread.yield();
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
					c = f.getComponents();
					Thread.yield();
				} while (c.length == 0);

				final Container p = c.length == 1 && c[0] instanceof Container ? (Container) c[0] : f;
				do {
					c = p.getComponents();

					for (final Component x : c) {
						final String s = x.getClass().getSimpleName();
						if (s.equals("Rs2Applet") || s.equals("client")) {
							if (!target.compareAndSet(null, x)) {
								continue;
							}
							final boolean rt4 = System.getProperty("com.jagex.config", "").startsWith("http://oldschool.");

							if (!rt4) {
								final BotOverlay o = new BotOverlay(BotLauncher.this);
								if (o.supported) {
									overlay.set(o);
								} else {
									o.dispose();
								}
							}

							bot.set(rt4 ? new org.powerbot.bot.rt4.Bot(BotLauncher.this) : new org.powerbot.bot.rt6.Bot(BotLauncher.this));
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

					Thread.yield();
				} while (c.length < 3);

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
					menu.set(new BotMenuBar(BotLauncher.this));
				}
				f.setMenuBar(menu.get());

				f.setSize(f.getMinimumSize());
				f.setLocationRelativeTo(f.getParent());

				if (Configuration.OS == Configuration.OperatingSystem.MAC) {
					new OSXAdapt(BotLauncher.this).run();
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
		final CryptFile cache = new CryptFile("version.1.txt");
		final int version;
		try {
			version = Integer.parseInt(IOUtils.readString(cache.download(new URL(Configuration.URLs.VERSION))).trim());
		} catch (final Exception e) {
			String msg = "Error reading server data";
			if (SocketException.class.isAssignableFrom(e.getClass()) || SocketTimeoutException.class.isAssignableFrom(e.getClass())) {
				msg = "Could not connect to " + Configuration.URLs.DOMAIN + " server";
			}
			JOptionPane.showMessageDialog(window.get(), msg, Configuration.NAME, JOptionPane.ERROR_MESSAGE);
			log.severe(msg);
			return false;
		}
		if (version > Configuration.VERSION) {
			final String msg = "A newer version is available, please download from " + BotLocale.WEBSITE;
			JOptionPane.showMessageDialog(window.get(), msg, Configuration.NAME, JOptionPane.WARNING_MESSAGE);
			log.severe(msg);
			return false;
		}
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
