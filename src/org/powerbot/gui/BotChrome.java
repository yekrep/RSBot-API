package org.powerbot.gui;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import org.powerbot.Configuration;
import org.powerbot.bot.Bot;
import org.powerbot.bot.SelectiveEventQueue;
import org.powerbot.gui.component.BotMenuBar;
import org.powerbot.gui.component.BotOverlay;
import org.powerbot.gui.component.BotPanel;
import org.powerbot.misc.CryptFile;
import org.powerbot.misc.OSXAdapt;
import org.powerbot.misc.Resources;
import org.powerbot.misc.Tracker;
import org.powerbot.misc.UpdateCheck;
import org.powerbot.misc.WindowCache;
import org.powerbot.util.Ini;

/**
 * @author Paris
 */
public class BotChrome extends JFrame implements Closeable {
	private static final Logger log = Logger.getLogger(BotChrome.class.getName());
	public static final int PANEL_MIN_WIDTH = 800, PANEL_MIN_HEIGHT = 600;
	private static final long serialVersionUID = -5535364874897541810L;

	private static BotChrome instance;
	private Bot bot;
	public BotPanel panel;
	public final BotOverlay overlay;
	public final BotMenuBar menuBar;
	private final WindowCache cache;

	private BotChrome() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (final Exception ignored) {
		}

		setTitle(Configuration.NAME);
		setIconImage(Resources.getImage(Resources.Paths.ICON));
		setBackground(Color.BLACK);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		setFocusTraversalKeysEnabled(false);

		setJMenuBar(menuBar = new BotMenuBar(this));

		panel = new BotPanel();
		add(panel);
		SelectiveEventQueue.getInstance().setBlocking(false);

		log.log(Level.INFO, "", "Starting...");
		pack();
		setMinimumSize(getSize());
		cache = new WindowCache(this);
		cache.run();
		setLocationRelativeTo(getParent());
		setVisible(true);
		new OSXAdapt(this).run();

		Tracker.getInstance().trackPage("", getTitle());

		overlay = new BotOverlay(this);

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent e) {
				overlay.adjustSize();
			}

			@Override
			public void componentMoved(final ComponentEvent e) {
				overlay.adjustSize();
			}
		});

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				close();
			}

			@Override
			public void windowDeiconified(final WindowEvent e) {
				if (overlay.isVisible()) {
					overlay.setVisible(false);
					overlay.setVisible(true);
				}
			}
		});

		Bot bot = null;
		if (new UpdateCheck().call()) {
			bot = new Bot(this);
			new Thread(bot.threadGroup, bot).start();
		}
		this.bot = bot;

		System.gc();
	}

	public static synchronized BotChrome getInstance() {
		if (instance == null) {
			instance = new BotChrome();
		}
		return instance;
	}

	public Bot getBot() {
		return bot;
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
		Tracker.getInstance().close();

		final int s = getExtendedState();
		final boolean maxed = (s & Frame.MAXIMIZED_VERT) == Frame.MAXIMIZED_VERT || (s & Frame.MAXIMIZED_HORIZ) == Frame.MAXIMIZED_HORIZ;

		if (!maxed) {
			cache.close();
		}

		if (bot != null) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					bot.stop();
				}
			}).start();
		}

		overlay.dispose();
		dispose();

		if (Configuration.OS == Configuration.OperatingSystem.WINDOWS) {
			System.exit(0);
			return;
		}

		final Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(6000);
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

	public void display(final Bot bot) {
		remove(panel);
		if (this.bot != null) {
			remove(bot.applet);
		}
		add(bot.applet);
		bot.applet.setSize(panel.getSize());
		overlay.setVisible(bot.applet != null && overlay.supported);
		invalidate();
		repaint();
	}
}