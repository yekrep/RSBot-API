package org.powerbot.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.FileLock;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.powerbot.Configuration;
import org.powerbot.bot.SelectiveEventQueue;
import org.powerbot.misc.CryptFile;
import org.powerbot.misc.GoogleAnalytics;
import org.powerbot.script.Bot;
import org.powerbot.script.Filter;
import org.powerbot.util.HttpUtils;
import org.powerbot.util.IOUtils;

public class BotChrome extends JFrame implements Closeable {
	private static final Logger log = Logger.getLogger(BotChrome.class.getName());
	public static final int PANEL_MIN_WIDTH = 800, PANEL_MIN_HEIGHT = 600;
	private static final long serialVersionUID = -5535364874897541810L;

	private static BotChrome instance;
	public final AtomicReference<Bot> bot;
	public final BotMenuBar menuBar;
	public final AtomicReference<BotOverlay> overlay;
	public final Component panel;
	private final Dimension size;
	private final AtomicReference<FileOutputStream> lockstream;
	private final AtomicReference<FileLock> lock;

	private BotChrome() {
		setTitle(Configuration.NAME);
		setBackground(Color.BLACK);
		getContentPane().setBackground(getBackground());
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		setFocusTraversalKeysEnabled(false);
		lockstream = new AtomicReference<FileOutputStream>(null);
		lock = new AtomicReference<FileLock>(null);

		SelectiveEventQueue.pushSelectiveQueue();
		SelectiveEventQueue.getInstance().setBlocking(false);

		bot = new AtomicReference<Bot>(null);
		overlay = new AtomicReference<BotOverlay>(null);
		add(panel = new BotPanel(this, new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return getMutex() && isLatestVersion();
			}
		}, new Filter<Bot>() {
			@Override
			public boolean accept(final Bot bot) {
				if (bot instanceof org.powerbot.bot.rt6.Bot) {
					overlay.set(new BotOverlay(BotChrome.this));
				}
				return BotChrome.this.bot.compareAndSet(null, bot);
			}
		}
		));
		setJMenuBar(menuBar = new BotMenuBar(this));

		pack();
		setMinimumSize(getSize());
		size = getMinimumSize();
		setLocationRelativeTo(getParent());

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				close();
			}
		});

		setVisible(true);

		if (Configuration.OS == Configuration.OperatingSystem.MAC) {
			new OSXAdapt(this).run();
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				final Image icon;
				try {
					icon = ImageIO.read(new CryptFile("icon.1.png").download(HttpUtils.openConnection(new URL(Configuration.URLs.ICON))));
				} catch (final IOException ignored) {
					return;
				}

				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						setIconImage(icon);
					}
				});

				if (Configuration.OS == Configuration.OperatingSystem.MAC) {
					OSXAdapt.setDockIconImage(icon);
				}
			}
		}).start();

		System.gc();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				GoogleAnalytics.getInstance().pageview("", getTitle());
			}
		});
	}

	public static synchronized BotChrome getInstance() {
		if (instance == null) {
			instance = new BotChrome();
		}
		return instance;
	}

	public void reset() {
		setTitle(Configuration.NAME);
		final BotOverlay o = overlay.get();
		if (o != null) {
			o.setVisible(false);
		}
		((BotPanel) panel).reset();
		setResizable(true);
		setMinimumSize(size);
		pack();
		log.info("Select a new game version");
		SelectiveEventQueue.getInstance().setBlocking(false);
	}

	private boolean getMutex() {
		try {
			lockstream.set(new FileOutputStream(new File(Configuration.HOME, "lock")));
			lock.set(lockstream.get().getChannel().tryLock());
			if (lock.get() == null) {
				throw new IOException();
			}
		} catch (final IOException ignored) {
			log.severe("Another instance is running on the same computer user account.\r\n"
					+ "Multiple bots have been disabled for security reasons.");
			return false;
		}

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

		if (lock.get() != null) {
			try {
				lock.getAndSet(null).release();
			} catch (final IOException ignored) {
			}
		}
		if (lockstream.get() != null) {
			try {
				lockstream.getAndSet(null).close();
			} catch (final IOException ignored) {
			}
		}

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
		dispose();

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
