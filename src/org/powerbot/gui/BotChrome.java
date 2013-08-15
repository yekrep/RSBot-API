package org.powerbot.gui;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.powerbot.Configuration;
import org.powerbot.bot.Bot;
import org.powerbot.gui.component.BotMenuBar;
import org.powerbot.gui.component.BotPanel;
import org.powerbot.service.UpdateCheck;
import org.powerbot.util.OSXAdapt;
import org.powerbot.util.Tracker;
import org.powerbot.util.io.CryptFile;
import org.powerbot.util.io.IniParser;
import org.powerbot.util.io.Resources;

/**
 * @author Paris
 */
public class BotChrome extends JFrame implements Closeable {
	private static BotChrome instance;
	private static Logger log = Logger.getLogger(BotChrome.class.getName());
	public static final int PANEL_MIN_WIDTH = 800, PANEL_MIN_HEIGHT = 600;
	public BotPanel panel;
	private final Bot bot;
	final CryptFile cache = new CryptFile("window-cache.1.ini", false, BotChrome.class);
	private static boolean minimised;

	private BotChrome() {
		setTitle(Configuration.NAME + (Configuration.BETA ? " Beta" : ""));
		setIconImage(Resources.getImage(Resources.Paths.ICON));
		setBackground(Color.BLACK);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				close();
			}

			@Override
			public void windowDeiconified(final WindowEvent e) {
				minimised = false;
			}

			@Override
			public void windowIconified(final WindowEvent e) {
				minimised = true;
			}
		});

		setJMenuBar(new BotMenuBar());

		panel = new BotPanel(this);
		add(panel);

		log.log(Level.INFO, "Optimising your experience", "Starting...");
		pack();
		setMinimumSize(getSize());
		setSize(getWindowCache());
		setLocationRelativeTo(getParent());
		setVisible(true);

		Tracker.getInstance().trackPage("", getTitle());

		final ExecutorService exec = Executors.newFixedThreadPool(1);
		final List<Future<Boolean>> tasks = new ArrayList<>();
		tasks.add(exec.submit(new OSXAdapt()));
		tasks.add(exec.submit(new UpdateCheck()));
		exec.shutdown();

		Bot bot = null;
		boolean pass = true;
		for (final Future<Boolean> task : tasks) {
			try {
				if (!task.get()) {
					pass = false;
				}
			} catch (final InterruptedException | ExecutionException ignored) {
			}
		}
		if (pass) {
			bot = new Bot();
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

	public boolean isMinimised() {
		return minimised;
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

	private void saveWindowCache() {
		final Map<String, String> data = new HashMap<>(2);
		data.put("w", Integer.toString(getWidth()));
		data.put("h", Integer.toString(getHeight()));
		final Map<String, Map<String, String>> map = new HashMap<>(1);
		map.put(IniParser.EMPTYSECTION, data);
		try (final OutputStream out = cache.getOutputStream()) {
			IniParser.serialise(map, out);
		} catch (final IOException ignored) {
			ignored.printStackTrace();
		}
	}

	private Dimension getWindowCache() {
		Dimension d = getSize();

		if (!cache.exists()) {
			return d;
		}

		Map<String, String> data = null;

		try (final InputStream in = cache.getInputStream()) {
			data = IniParser.deserialise(in).get(IniParser.EMPTYSECTION);
		} catch (final IOException ignored) {
		}

		if (data == null) {
			return d;
		}

		int w = d.width, h = d.height;

		if (data.containsKey("w")) {
			try {
				w = Integer.parseInt(data.get("w"));
			} catch (final NumberFormatException ignored) {
			}
		}

		if (data.containsKey("h")) {
			try {
				h = Integer.parseInt(data.get("h"));
			} catch (final NumberFormatException ignored) {
			}
		}

		return new Dimension(w, h);
	}

	@Override
	public void close() {
		log.info("Shutting down");

		final int s = getExtendedState();
		final boolean maxed = (s & Frame.MAXIMIZED_VERT) == Frame.MAXIMIZED_VERT || (s & Frame.MAXIMIZED_HORIZ) == Frame.MAXIMIZED_HORIZ;

		if (!maxed) {
			saveWindowCache();
		}

		setVisible(false);
		dispose();
		System.exit(0);
	}
}
