package org.powerbot.gui;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Frame;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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

import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.WindowConstants;

import org.powerbot.Configuration;
import org.powerbot.bot.SelectiveEventQueue;
import org.powerbot.misc.CryptFile;
import org.powerbot.misc.Resources;
import org.powerbot.misc.Tracker;
import org.powerbot.script.Bot;
import org.powerbot.script.Filter;
import org.powerbot.util.IOUtils;

public class BotChrome extends JFrame implements Closeable {
	private static final Logger log = Logger.getLogger(BotChrome.class.getName());
	public static final int PANEL_MIN_WIDTH = 800, PANEL_MIN_HEIGHT = 600;
	private static final long serialVersionUID = -5535364874897541810L;

	private static BotChrome instance;
	public final AtomicReference<Bot> bot;
	public final BotMenuBar menuBar;
	private final AtomicReference<BotOverlay> overlay;
	private final WindowCache cache;

	private BotChrome() {
		setTitle(Configuration.NAME);
		setIconImage(Resources.getImage(Resources.Paths.ICON));
		setBackground(Color.BLACK);
		getContentPane().setBackground(getBackground());
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		setFocusTraversalKeysEnabled(false);

		bot = new AtomicReference<Bot>(null);
		overlay = new AtomicReference<BotOverlay>(null);
		add(new BotPanel(this, new Filter<Bot>() {
			@Override
			public boolean accept(final Bot bot) {
				if (bot instanceof org.powerbot.bot.rs3.Bot) {
					overlay.set(new BotOverlay(BotChrome.this));
				}
				return BotChrome.this.bot.compareAndSet(null, bot);
			}
		}));
		setJMenuBar(menuBar = new BotMenuBar(this));
		SelectiveEventQueue.getInstance().setBlocking(false);

		pack();
		setMinimumSize(getSize());
		cache = new WindowCache(this, "chrome");
		cache.run();
		setLocationRelativeTo(getParent());
		setVisible(true);
		new OSXAdapt(this).run();
		Tracker.getInstance().trackPage("", getTitle());

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				close();
			}
		});

		System.gc();
	}

	public static synchronized BotChrome getInstance() {
		if (instance == null) {
			instance = new BotChrome();
		}
		return instance;
	}

	private Boolean isLatestVersion() {
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

		final int s = getExtendedState();
		final boolean maxed = (s & Frame.MAXIMIZED_VERT) == Frame.MAXIMIZED_VERT || (s & Frame.MAXIMIZED_HORIZ) == Frame.MAXIMIZED_HORIZ;

		if (!maxed && (bot.get() == null || !(bot.get() instanceof org.powerbot.bot.os.Bot))) {
			cache.close();
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
