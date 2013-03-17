package org.powerbot.gui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.WindowConstants;

import org.powerbot.bot.Bot;
import org.powerbot.gui.component.BotLocale;
import org.powerbot.gui.component.BotLogPane;
import org.powerbot.gui.component.BotPanel;
import org.powerbot.gui.component.BotToolBar;
import org.powerbot.gui.controller.BotInteract;
import org.powerbot.ipc.ScheduledChecks;
import org.powerbot.service.NetworkAccount;
import org.powerbot.util.Configuration;
import org.powerbot.util.LoadAds;
import org.powerbot.util.LoadUpdates;
import org.powerbot.util.Tracker;
import org.powerbot.util.io.Resources;

/**
 * @author Paris
 */
public class BotChrome extends JFrame implements WindowListener {
	private static final long serialVersionUID = 1L;
	private static BotChrome instance;
	private static Logger log = Logger.getLogger(BotChrome.class.getName());
	public static final int PANEL_WIDTH = 765, PANEL_HEIGHT = 553;
	public BotPanel panel;
	public BotToolBar toolbar;
	public JScrollPane logpane;
	public static volatile boolean loaded = false;
	public static volatile boolean minimised = false;

	private BotChrome() {
		setTitle(Configuration.TITLE + (Configuration.BETA ? " Beta" : ""));
		setIconImage(Resources.getImage(Resources.Paths.ICON));
		addWindowListener(this);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(final Thread t, final Throwable e) {
				log.log(Level.SEVERE, "Uncaught exception on " + t.getName() + "@" + Long.toHexString(t.getId()) + ": ", e);
			}
		});

		toolbar = new BotToolBar(this);
		toolbar.setVisibleEx(false);
		add(toolbar, BorderLayout.NORTH);
		panel = new BotPanel(this);
		add(panel);

		final BotLogPane logtextpane = new BotLogPane();
		logpane = new JScrollPane(logtextpane);
		logpane.setPreferredSize(logtextpane.getPreferredSize());
		logpane.setVisible(false);
		add(logpane, BorderLayout.SOUTH);

		log.log(Level.INFO, "Firing up the engines", BotLocale.STARTING);
		pack();
		setResizable(false);
		setMinimumSize(getSize());
		setLocationRelativeTo(getParent());
		setVisible(true);

		Tracker.getInstance().trackPage("", getTitle());

		final ExecutorService exec = Executors.newFixedThreadPool(1);
		final List<Future<Boolean>> tasks = new ArrayList<Future<Boolean>>();
		tasks.add(exec.submit(new LoadUpdates()));
		tasks.add(exec.submit(new LoadAccount()));
		tasks.add(exec.submit(new LoadAds()));
		exec.execute(new LoadComplete(this, tasks));
		exec.shutdown();
	}

	public static BotChrome getInstance() {
		if (instance == null) {
			instance = new BotChrome();
		}
		return instance;
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

	public void windowActivated(final WindowEvent arg0) {
	}

	public void windowClosed(final WindowEvent arg0) {
	}

	public void windowClosing(final WindowEvent arg0) {
		log.info("Shutting down");
		setVisible(false);
		if (Bot.instantiated()) {
			Bot.getInstance().stop();
		}
		dispose();
		System.exit(0);
	}

	public void windowDeactivated(final WindowEvent arg0) {
	}

	public void windowDeiconified(final WindowEvent arg0) {
		minimised = false;
	}

	public void windowIconified(final WindowEvent arg0) {
		minimised = true;
	}

	public void windowOpened(final WindowEvent arg0) {
	}

	private final class LoadAccount implements Callable<Boolean> {
		public Boolean call() throws Exception {
			log.log(Level.INFO, "Signing into " + BotLocale.WEBSITE, BotLocale.STARTING);
			NetworkAccount.getInstance();
			return true;
		}
	}

	private final class LoadComplete implements Runnable {
		private final BotChrome parent;
		private final List<Future<Boolean>> tasks;

		public LoadComplete(final BotChrome parent, final List<Future<Boolean>> tasks) {
			this.parent = parent;
			this.tasks = tasks;
		}

		public void run() {
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
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						final Timer timer = new Timer(1000 * 60 * 1, new ScheduledChecks());
						timer.setCoalesce(false);
						timer.start();

						toolbar.registerPreferences();
						toolbar.setVisibleEx(true);
						parent.validate();
						parent.repaint();

						BotSignin.showWelcomeMessage();

						if (Configuration.BETA) {
							final String s = "This is a beta version for developers only and certain features have been disabled.\nDo not use this version for general purposes, you have been warned.";
							JOptionPane.showMessageDialog(BotChrome.getInstance(), s, "Beta", JOptionPane.WARNING_MESSAGE);
						}

						if (NetworkAccount.getInstance().hasPermission(NetworkAccount.VIP)) {
							BotInteract.tabAdd();
						}
					}
				});
			}
			System.gc();
			loaded = true;
		}
	}
}
