package org.powerbot.gui.component;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.powerbot.Boot;
import org.powerbot.Configuration;
import org.powerbot.bot.Bot;
import org.powerbot.event.BotMenuListener;
import org.powerbot.gui.BotAccounts;
import org.powerbot.gui.BotChrome;
import org.powerbot.gui.BotScripts;
import org.powerbot.gui.BotSignin;
import org.powerbot.misc.NetworkAccount;
import org.powerbot.misc.Resources;
import org.powerbot.misc.Tracker;
import org.powerbot.script.Script;
import org.powerbot.script.internal.ScriptController;

public class BotMenuBar extends JMenuBar {
	private static final long serialVersionUID = -4186554435386744949L;
	private final BotChrome chrome;
	private final JMenuItem signin, play, stop;

	public BotMenuBar(final BotChrome chrome) {
		this.chrome = chrome;

		final JMenu file = new JMenu(BotLocale.FILE), edit = new JMenu(BotLocale.EDIT), view = new JMenu(BotLocale.VIEW),
				script = new JMenu(BotLocale.SCRIPTS), input = new JMenu(BotLocale.INPUT), help = new JMenu(BotLocale.HELP);

		final JMenuItem newtab = new JMenuItem(BotLocale.NEWWINDOW);
		file.add(newtab);
		newtab.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				Boot.fork();
			}
		});

		if (Configuration.OS != Configuration.OperatingSystem.MAC) {
			file.addSeparator();
			final JMenuItem exit = new JMenuItem(BotLocale.EXIT);
			file.add(exit);
			exit.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					chrome.close();
				}
			});
		}

		signin = new JMenuItem(BotLocale.SIGNIN);
		edit.add(signin);
		signin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				new BotSignin(chrome);
			}
		});
		final JMenuItem accounts = new JMenuItem(BotLocale.ACCOUNTS);
		edit.add(accounts);
		accounts.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				new BotAccounts(chrome);
			}
		});

		edit.addMenuListener(new MenuListener() {
			@Override
			public void menuSelected(final MenuEvent e) {
				final NetworkAccount account = NetworkAccount.getInstance();
				signin.setText(account.isLoggedIn() ? account.getDisplayName() + "..." : BotLocale.SIGNIN);
			}

			@Override
			public void menuDeselected(final MenuEvent e) {
			}

			@Override
			public void menuCanceled(final MenuEvent e) {
			}
		});

		view.addMenuListener(new MenuListener() {
			@Override
			public void menuSelected(final MenuEvent e) {
				final JMenu menu = (JMenu) e.getSource();
				menu.removeAll();
				new BotMenuView(chrome, menu);
			}

			@Override
			public void menuDeselected(final MenuEvent e) {
			}

			@Override
			public void menuCanceled(final MenuEvent e) {
			}
		});

		final ImageIcon[] playIcons = new ImageIcon[]{new ImageIcon(Resources.getImage(Resources.Paths.PLAY)), new ImageIcon(Resources.getImage(Resources.Paths.PAUSE))};
		play = new JMenuItem(BotLocale.PLAYSCRIPT);
		play.setIcon(playIcons[0]);
		script.add(play);
		play.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				scriptPlayPause();
			}
		});
		stop = new JMenuItem(BotLocale.STOPSCRIPT);
		stop.setIcon(new ImageIcon(Resources.getImage(Resources.Paths.STOP)));
		script.add(stop);
		stop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				scriptStop();
			}
		});

		script.addSeparator();
		final JMenu options = new JMenu(BotLocale.OPTIONS);
		script.add(options);

		options.addMenuListener(new MenuListener() {
			@Override
			public void menuSelected(final MenuEvent e) {
				final JMenu m = (JMenu) e.getSource();
				m.removeAll();

				final ScriptController c = chrome.getBot().controller;
				if (!c.isValid()) {
					return;
				}

				final Script s = c.bundle.get().instance.get();
				if (s == null || !(s instanceof BotMenuListener)) {
					return;
				}

				try {
					((BotMenuListener) s).menuSelected(e);
				} catch (final Throwable t) {
					t.printStackTrace();
				}
			}

			@Override
			public void menuDeselected(final MenuEvent e) {
				final ScriptController c = chrome.getBot().controller;
				if (!c.isValid()) {
					return;
				}

				final Script s = c.bundle.get().instance.get();
				if (s == null || !(s instanceof BotMenuListener)) {
					return;
				}

				try {
					((BotMenuListener) s).menuDeselected(e);
				} catch (final Throwable t) {
					t.printStackTrace();
				}
			}

			@Override
			public void menuCanceled(final MenuEvent e) {
				final ScriptController c = chrome.getBot().controller;
				if (!c.isValid()) {
					return;
				}

				final Script s = c.bundle.get().instance.get();
				if (s == null || !(s instanceof BotMenuListener)) {
					return;
				}

				try {
					((BotMenuListener) s).menuCanceled(e);
				} catch (final Throwable t) {
					t.printStackTrace();
				}
			}
		});

		script.addMenuListener(new MenuListener() {
			@Override
			public void menuSelected(final MenuEvent e) {
				final ScriptController c = chrome.getBot().controller;
				final boolean active = c.isValid() && !c.isStopping(), running = active && !c.isSuspended();

				play.setEnabled(chrome.getBot().ctx.getClient() != null && !BotScripts.loading.get());
				play.setText(running ? BotLocale.PAUSESCRIPT : active ? BotLocale.RESUMESCRIPT : BotLocale.PLAYSCRIPT);
				play.setIcon(playIcons[running ? 1 : 0]);
				stop.setEnabled(active);

				if (active) {
					final Script script = c.bundle.get().instance.get();
					options.setEnabled(script != null && script instanceof BotMenuListener);
				} else {
					options.setEnabled(false);
				}
			}

			@Override
			public void menuDeselected(final MenuEvent e) {
			}

			@Override
			public void menuCanceled(final MenuEvent e) {
			}
		});

		input.addMenuListener(new MenuListener() {
			@Override
			public void menuSelected(final MenuEvent e) {
				final JMenu menu = (JMenu) e.getSource();
				if (menu.getItemCount() != 0) {
					menu.removeAll();
				}
				new BotMenuInput(menu);
			}

			@Override
			public void menuDeselected(final MenuEvent e) {
			}

			@Override
			public void menuCanceled(final MenuEvent e) {
			}
		});

		if (Configuration.OS != Configuration.OperatingSystem.MAC) {
			final JMenuItem about = new JMenuItem(BotLocale.ABOUT);
			help.add(about);
			about.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					showAbout();
				}
			});
		}

		final JMenuItem license = new JMenuItem(BotLocale.LICENSE);
		help.add(license);
		license.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				showLicense();
			}
		});

		add(file);
		add(edit);
		add(view);
		add(script);
		add(input);
		add(help);
	}

	public void showAbout() {
		final String year = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
		final String msg = "<b>" + Configuration.NAME + " " + Integer.toString(Configuration.VERSION) + "</b>\n\n" +
				"Copyright \u00a9 2011 - " + year + " Dequeue Ltd and its licensors.\n" +
				"By using this software you agree to be bound by the terms of the license agreement.\n\n" +
				"RuneScape\u00ae is a trademark of Jagex \u00a9 1999 - " + year + " Jagex Ltd.\n" +
				"RuneScape content and materials are trademarks and copyrights of Jagex or its licensees.\n" +
				"This program is not affiliated with Jagex Ltd., nor do they endorse usage of our software.";
		final JLabel text = new JLabel("<html>" + msg.replace("\n", "<br>") + "</html>");
		final Font f = text.getFont();
		text.setFont(new Font(f.getName(), f.getStyle(), f.getSize() - 2));
		JOptionPane.showMessageDialog(chrome, text, BotLocale.ABOUT, JOptionPane.PLAIN_MESSAGE);
	}

	public void showLicense() {
		BotChrome.openURL(Configuration.URLs.LICENSE);
	}

	public synchronized void scriptPlayPause() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				final Bot bot = chrome.getBot();
				final ScriptController c = chrome.getBot().controller;

				if (c.isValid()) {
					if (c.isSuspended()) {
						c.resume();
					} else {
						c.suspend();
					}
				} else {
					if (bot.ctx.getClient() != null) {
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								new BotScripts(chrome);
							}
						});
					}
				}
			}
		}).start();
	}

	public synchronized void scriptStop() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				chrome.getBot().controller.stop();
			}
		}).start();
	}
}
