package org.powerbot.gui;

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;

import org.powerbot.bot.Bot;
import org.powerbot.gui.component.*;
import org.powerbot.gui.controller.BotInteract;
import org.powerbot.ipc.Controller;
import org.powerbot.ipc.ScheduledChecks;
import org.powerbot.service.NetworkAccount;
import org.powerbot.util.*;
import org.powerbot.util.io.Resources;

/**
 * @author Paris
 */
public class BotChrome extends JFrame implements WindowListener {
	private static final long serialVersionUID = 1L;
	private static BotChrome instance;
	private static final Logger log = Logger.getLogger(BotChrome.class.getName());
	public static final int PANEL_WIDTH = 765, PANEL_HEIGHT = 553;
	public final BotPanel panel;
	public final JScrollPane logpane;
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

		setJMenuBar(new BotMenuBar());
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
		final List<Future<Boolean>> tasks = new ArrayList<>();
		tasks.add(exec.submit(new LoadUpdates()));
		tasks.add(exec.submit(new LoadOSX()));
		tasks.add(exec.submit(new LoadAccount()));
		exec.execute(new LoadComplete(this, tasks));
		exec.shutdown();
	}

	public static BotChrome getInstance() {
		if (instance == null) {
			instance = new BotChrome();
		}
		return instance;
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
		if (NetworkAccount.getInstance().isLoggedIn()) {
			NetworkAccount.getInstance().sessionQuery(Controller.getInstance().getRunningInstances() - 1);
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
			final NetworkAccount net = NetworkAccount.getInstance();
			if (net.isLoggedIn() && !net.session()) {
				net.logout();
			}
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

						parent.validate();
						parent.repaint();

						BotSignin.showWelcomeMessage();

						if (Configuration.BETA) {
							final String s = "This is a beta version for developers only and certain features have been disabled.\nDo not use this version for general purposes, you have been warned.";
							if (!Configuration.SUPERDEV) {
								JOptionPane.showMessageDialog(BotChrome.getInstance(), s, "Beta", JOptionPane.WARNING_MESSAGE);
							}
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
