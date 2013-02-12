package org.powerbot.gui.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.powerbot.Boot;
import org.powerbot.core.Bot;
import org.powerbot.core.bot.handlers.ScriptHandler;
import org.powerbot.core.script.job.Task;
import org.powerbot.core.script.methods.Game;
import org.powerbot.gui.BotAbout;
import org.powerbot.gui.BotAccounts;
import org.powerbot.gui.BotChrome;
import org.powerbot.gui.BotScripts;
import org.powerbot.gui.BotSignin;
import org.powerbot.ipc.Controller;
import org.powerbot.service.NetworkAccount;
import org.powerbot.util.Configuration;
import org.powerbot.util.io.Resources;

/**
 * @author Paris
 */
public final class BotMenu extends JPopupMenu implements ActionListener {
	private static final long serialVersionUID = 1L;
	private final JMenuItem signin;

	public BotMenu() {
		final int tabs = Bot.instantiated() ? 1 : 0, inst = Controller.getInstance().getRunningInstances();

		final JMenuItem newtab = new JMenuItem(BotLocale.NEWTAB);
		BotKeyEventDispatcher.setAccelerator(newtab, BotKeyEventDispatcher.Action.TAB_ADD);
		newtab.setIcon(new ImageIcon(Resources.getImage(Resources.Paths.ADD)));
		newtab.addActionListener(this);
		final JMenuItem closetab = new JMenuItem(BotLocale.CLOSETAB);
		BotKeyEventDispatcher.setAccelerator(closetab, BotKeyEventDispatcher.Action.TAB_CLOSE);
		closetab.setIcon(new ImageIcon(Resources.getImage(Resources.Paths.REMOVE)));
		closetab.setEnabled(tabs > 0 || inst > 1);
		closetab.addActionListener(this);
		add(newtab);
		add(closetab);
		addSeparator();

		final JMenuItem accounts = new JMenuItem(BotLocale.ACCOUNTS);
		BotKeyEventDispatcher.setAccelerator(accounts, BotKeyEventDispatcher.Action.ACCOUNTS);
		accounts.setIcon(new ImageIcon(Resources.getImage(Resources.Paths.ADDRESS)));
		accounts.addActionListener(this);
		add(accounts);

		signin = new JMenuItem(BotLocale.SIGNIN + "...");
		BotKeyEventDispatcher.setAccelerator(signin, BotKeyEventDispatcher.Action.SIGNIN);
		signin.setIcon(new ImageIcon(Resources.getImage(Resources.Paths.KEYS)));
		if (NetworkAccount.getInstance().isLoggedIn()) {
			signin.setText(NetworkAccount.getInstance().getAccount().getDisplayName());
		}
		add(signin);
		signin.addActionListener(this);
		addSeparator();

		boolean running = false;
		if (Bot.instantiated()) {
			final Bot bot = Bot.instance();
			final ScriptHandler script = bot.getScriptHandler();
			running = script != null && script.isActive() && !script.isPaused();
		}
		final JMenuItem play = new JMenuItem(running ? BotLocale.PAUSESCRIPT : BotLocale.PLAYSCRIPT);
		play.setEnabled(Bot.instantiated());
		BotKeyEventDispatcher.setAccelerator(play, BotKeyEventDispatcher.Action.SCRIPT_PLAYPAUSE);
		play.setIcon(new ImageIcon(Resources.getImage(running ? Resources.Paths.PAUSE : Resources.Paths.PLAY)));
		add(play);
		final JMenuItem stop = new JMenuItem(BotLocale.STOPSCRIPT);
		stop.setEnabled(Bot.instantiated());
		BotKeyEventDispatcher.setAccelerator(stop, BotKeyEventDispatcher.Action.SCRIPT_STOP);
		stop.setIcon(new ImageIcon(Resources.getImage(Resources.Paths.STOP)));
		add(stop);
		addSeparator();

		add(new BotMenuView(this));
		addSeparator();

		final JMenuItem about = new JMenuItem(BotLocale.ABOUT);
		BotKeyEventDispatcher.setAccelerator(about, BotKeyEventDispatcher.Action.ABOUT);
		about.setIcon(new ImageIcon(Resources.getImage(Resources.Paths.INFO)));
		about.addActionListener(this);
		add(about);
	}

	public void actionPerformed(final ActionEvent e) {
		switch (e.getActionCommand()) {
		case BotLocale.NEWTAB: tabAdd(); break;
		case BotLocale.CLOSETAB: tabClose(false); break;
		case BotLocale.ACCOUNTS: showDialog(BotKeyEventDispatcher.Action.ACCOUNTS); break;
		case BotLocale.PLAYSCRIPT: scriptPlayPause(); break;
		case BotLocale.STOPSCRIPT: scriptStop(); break;
		case BotLocale.ABOUT: showDialog(BotKeyEventDispatcher.Action.ABOUT); break;
		default:
			if (e.getSource() == signin) {
				showDialog(BotKeyEventDispatcher.Action.SIGNIN);
			}
			break;
		}
	}

	public static void showDialog(final BotKeyEventDispatcher.Action action) {
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
				if (!NetworkAccount.getInstance().hasPermission(NetworkAccount.Permissions.VIP) && Configuration.isServerOS()) {
					log.info(BotLocale.NEEDVIPVPS);
				} else if (n > 0 && !NetworkAccount.getInstance().isLoggedIn()) {
					log.severe(BotLocale.NEEDSIGNINMULTITAB);
				} else if (n > 2 && !NetworkAccount.getInstance().hasPermission(NetworkAccount.Permissions.VIP)) {
					log.severe(BotLocale.NEEDVIPMULTITAB);
				} else {
					if (s > 0) {
						Boot.fork(Boot.SWITCH_NEWTAB);
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
					chrome.panel.setBot(null);
					Bot.instance().stop();
					chrome.panel.repaint();
					Logger.getLogger(Bot.class.getName()).log(Level.INFO, "Add a tab to start another bot", "Closed");
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
				script.resume();
			} else {
				script.pause();
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
		final Bot bot = Bot.instance();
		final ScriptHandler activeScript = bot.getScriptHandler();
		if (activeScript != null) {
			if (!activeScript.isShutdown()) {
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
					activeScript.stop();
				}
			}
		}
	}
}
