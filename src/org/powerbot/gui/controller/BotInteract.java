package org.powerbot.gui.controller;

import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.powerbot.Boot;
import org.powerbot.core.Bot;
import org.powerbot.core.script.internal.ScriptHandler;
import org.powerbot.core.script.job.Task;
import org.powerbot.core.script.methods.Game;
import org.powerbot.gui.BotAbout;
import org.powerbot.gui.BotAccounts;
import org.powerbot.gui.BotChrome;
import org.powerbot.gui.BotScripts;
import org.powerbot.gui.BotSignin;
import org.powerbot.gui.component.BotLocale;
import org.powerbot.ipc.Controller;
import org.powerbot.service.NetworkAccount;
import org.powerbot.util.Configuration;
import org.powerbot.util.Tracker;

/**
 * @author Paris
 */
public final class BotInteract {
	public static enum Action { MENU, TAB_ADD, TAB_CLOSE, ACCOUNTS, SIGNIN, ABOUT, SCRIPT_PLAYPAUSE, SCRIPT_STOP };

	public static void showDialog(final Action action) {
		final BotChrome chrome = BotChrome.getInstance();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				switch (action)
				{
				case ACCOUNTS: new BotAccounts(chrome); break;
				case SIGNIN: new BotSignin(chrome); break;
				case ABOUT: new BotAbout(chrome); break;
				default: break;
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
						final Bot bot = Bot.instance();
						new Thread(bot.threadGroup, bot).start();
						BotChrome.getInstance().panel.setBot(bot);
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
					final BotChrome chrome = BotChrome.getInstance();
					if (!silent) {
						try {
							if (Game.isLoggedIn() && JOptionPane.showConfirmDialog(chrome, "Are you sure you want to close this tab?", BotLocale.CLOSETAB, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) != JOptionPane.YES_OPTION) {
								return;
							}
						} catch (final RuntimeException ignored) {
						}
					}
					Tracker.getInstance().trackEvent("tab", "add", silent ? "silent" : "");
					chrome.panel.setBot(null);
					Bot.instance().stop();
					chrome.panel.repaint();
					Logger.getLogger(Bot.class.getName()).log(Level.INFO, "Add a tab to start another bot", "Closed");
					parent.toolbar.updateControls();
					System.gc();
				} else {
					parent.dispatchEvent(new WindowEvent(parent, WindowEvent.WINDOW_CLOSING));
				}
			}
		});
	}

	public static synchronized void scriptPlayPause() {
		final Bot bot = Bot.instance();
		final ScriptHandler script = bot.getScriptHandler();
		if (script != null && script.isActive()) {
			if (script.isPaused()) {
				Tracker.getInstance().trackEvent("script", "resume");
				script.resume();
			} else {
				Tracker.getInstance().trackEvent("script", "pause");
				script.pause();
			}
			BotChrome.getInstance().toolbar.updateControls();
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
		final Bot bot = Bot.instance();
		final ScriptHandler activeScript = bot.getScriptHandler();
		if (activeScript != null) {
			if (!activeScript.isShutdown()) {
				Tracker.getInstance().trackEvent("script", "stop");
				bot.stopScript();
				new Thread(bot.threadGroup, new Runnable() {
					public void run() {
						while (activeScript.isActive()) {
							Task.sleep(150);
						}
					}
				}).start();
			} else {
				if (activeScript.isActive()) {
					activeScript.log.info("Forcing script stop");
					Tracker.getInstance().trackEvent("script", "stop", "force");
					activeScript.stop();
				}
			}
		}
		BotChrome.getInstance().toolbar.updateControls();
	}
}
