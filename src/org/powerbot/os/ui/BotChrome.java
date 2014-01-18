package org.powerbot.os.ui;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.WindowConstants;

import org.powerbot.os.Configuration;
import org.powerbot.os.client.Bot;
import org.powerbot.os.misc.OSXAdapt;

/**
 * @author Paris
 */
public class BotChrome extends JFrame implements Closeable {
	private static final Logger log = Logger.getLogger(BotChrome.class.getSimpleName());
	public final AtomicReference<Bot> bot;
	public final BotMenuBar menu;
	public final BotPanel panel;

	public BotChrome() {
		setTitle(Configuration.NAME);
		setBackground(Color.BLACK);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		setFocusTraversalKeysEnabled(false);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				close();
			}
		});

		setJMenuBar(menu = new BotMenuBar(this));
		add(panel = new BotPanel());

		if (Configuration.OS == Configuration.OperatingSystem.MAC) {
			new OSXAdapt(this).run();
		}

		setSize(new Dimension(765, 503));
		setPreferredSize(getSize());
		setMinimumSize(getSize());
		setLocationRelativeTo(getParent());
		setResizable(false);
		setVisible(true);

		bot = new AtomicReference<Bot>(new Bot(this));
		new Thread(bot.get()).start();
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
		setVisible(false);

		if (bot.get() != null) {
			bot.get().close();
		}

		dispose();
	}
}
