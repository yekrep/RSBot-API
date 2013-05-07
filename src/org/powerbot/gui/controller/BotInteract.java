package org.powerbot.gui.controller;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;

import org.powerbot.Boot;
import org.powerbot.bot.Bot;
import org.powerbot.gui.*;
import org.powerbot.gui.component.BotLocale;
import org.powerbot.ipc.Controller;
import org.powerbot.script.internal.ScriptManager;
import org.powerbot.script.xenon.Game;
import org.powerbot.service.NetworkAccount;
import org.powerbot.util.Configuration;
import org.powerbot.util.Tracker;

/**
 * @author Paris
 */
public final class BotInteract {
	public static void showDialog(final Action action) {
		final BotChrome chrome = BotChrome.getInstance();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				switch (action) {
				case ACCOUNTS:
					new BotAccounts(chrome);
					break;
				case SIGNIN:
					new BotSignin(chrome);
					break;
				case ABOUT:
					new BotAbout(chrome);
					break;
				case LICENSE:
					openURL(Configuration.URLs.LICENSE);
					break;
				default:
					break;
				}
			}
		});
	}

	public static synchronized void tabAdd() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				final int s = Bot.instantiated() ? 1 : 0;
				final int n = Controller.getInstance().getRunningInstances();
				final Logger log = Logger.getLogger(BotChrome.class.getName());
				log.info(BotLocale.LOADINGTAB);
				if (!NetworkAccount.getInstance().hasPermission(NetworkAccount.VIP) && Configuration.isServerOS()) {
					log.info(BotLocale.NEEDVIPVPS);
				} else if (n > 0 && !NetworkAccount.getInstance().isLoggedIn()) {
					log.severe(BotLocale.NEEDSIGNINMULTITAB);
				} else if (n > 2 && !NetworkAccount.getInstance().hasPermission(NetworkAccount.VIP)) {
					log.severe(BotLocale.NEEDVIPMULTITAB);
				} else {
					Tracker.getInstance().trackEvent("tab", "add");
					if (s > 0) {
						Boot.fork();
					} else {
						final Bot bot = Bot.getInstance();
						new Thread(bot.threadGroup, bot).start();
					}
				}
			}
		});
	}

	public static synchronized void tabClose(final boolean silent) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				final BotChrome parent = BotChrome.getInstance();
				if (Bot.instantiated()) {
					if (!silent) {
						try {
							if (Game.isLoggedIn() && JOptionPane.showConfirmDialog(parent, "Are you sure you want to close this tab?", BotLocale.CLOSETAB, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) != JOptionPane.YES_OPTION) {
								return;
							}
						} catch (final RuntimeException ignored) {
						}
					}
					Tracker.getInstance().trackEvent("tab", "add", silent ? "silent" : "");
					Bot.getInstance().stop();
					parent.panel.repaint();
					Logger.getLogger(Bot.class.getName()).log(Level.INFO, "Add a tab to start another bot", "Closed");
					System.gc();
				} else {
					parent.dispatchEvent(new WindowEvent(parent, WindowEvent.WINDOW_CLOSING));
				}
			}
		});
	}

	public static synchronized void scriptPlayPause() {
		final Bot bot = Bot.getInstance();
		final ScriptManager container = bot.getScriptController();
		if (container != null) {
			if (container.isSuspended()) {
				Tracker.getInstance().trackEvent("script", "resume");
				container.resume();
			} else {
				Tracker.getInstance().trackEvent("script", "pause");
				container.suspend();
			}
			return;
		}

		if (Bot.client() != null) {
			new BotScripts(BotChrome.getInstance());
		}
	}

	public static synchronized void scriptStop() {
		if (!Bot.instantiated()) {
			return;
		}
		final Bot bot = Bot.getInstance();
		final ScriptManager container = bot.getScriptController();
		if (container != null) {
			if (!container.isStopping()) {
				Tracker.getInstance().trackEvent("script", "stop");
				bot.stopScripts();
			}
		}
	}

	public static boolean toggleLogPane() {
		final BotChrome parent = BotChrome.getInstance();
		parent.logpane.setVisible(!parent.logpane.isVisible());
		final int[] h = { parent.logpane.getSize().height, parent.logpane.getPreferredSize().height };
		parent.setSize(new Dimension(parent.getSize().width, parent.getSize().height + h[h[0] == 0 ? 1 : 0] * (parent.logpane.isVisible() ? 1 : -1)));
		return parent.logpane.isVisible();
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

	public static enum Action {MENU, TAB_ADD, TAB_CLOSE, ACCOUNTS, SIGNIN, ABOUT, LICENSE, SCRIPT_PLAYPAUSE, SCRIPT_STOP}
}
