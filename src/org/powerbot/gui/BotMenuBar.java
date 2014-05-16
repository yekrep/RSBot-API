package org.powerbot.gui;

import java.awt.CheckboxMenuItem;
import java.awt.Font;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.powerbot.Boot;
import org.powerbot.Configuration;
import org.powerbot.bot.InputSimulator;
import org.powerbot.bot.ScriptController;
import org.powerbot.bot.SelectiveEventQueue;
import org.powerbot.misc.GoogleAnalytics;
import org.powerbot.misc.ScriptBundle;
import org.powerbot.script.Bot;
import org.powerbot.script.BotMenuActionListener;
import org.powerbot.script.Script;

public class BotMenuBar extends MenuBar {
	private static final long serialVersionUID = -4186554435386744949L;
	private final BotLauncher launcher;
	private final Menu view;
	private final MenuItem play, stop;
	private final CheckboxMenuItem inputAllow, inputBlock;

	public BotMenuBar(final BotLauncher launcher) {
		this.launcher = launcher;

		final Menu file = new Menu(BotLocale.FILE), edit = new Menu(BotLocale.EDIT),
				input = new Menu(BotLocale.INPUT), help = new Menu(BotLocale.HELP);

		view = new Menu(BotLocale.VIEW);
		view.setEnabled(false);

		final MenuItem newtab = new MenuItem(BotLocale.NEW_WINDOW);
		file.add(newtab);
		newtab.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				Boot.fork();
			}
		});

		if (Configuration.OS != Configuration.OperatingSystem.MAC) {
			file.addSeparator();
			final MenuItem exit = new MenuItem(BotLocale.EXIT);
			file.add(exit);
			exit.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					launcher.close();
				}
			});
		}

		edit.setEnabled(false);
		play = new MenuItem(BotLocale.SCRIPT_PLAY);
		edit.add(play);
		play.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				scriptPlayPause();
			}
		});
		stop = new MenuItem(BotLocale.SCRIPT_STOP);
		edit.add(stop);
		stop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				scriptStop();
			}
		});
		edit.addSeparator();
		final MenuItem options = new MenuItem(BotLocale.OPTIONS);
		edit.add(options);
		options.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				final ScriptController c = launcher.bot.get() == null ? null : (ScriptController) launcher.bot.get().ctx.controller;
				if (c == null || !c.valid()) {
					return;
				}

				final Script s = ((ScriptBundle) c.bundle.get()).instance.get();
				if (s == null || !(s instanceof BotMenuActionListener)) {
					return;
				}

				try {
					((BotMenuActionListener) s).actionPerformed(e);
				} catch (final Throwable t) {
					t.printStackTrace();
				}
			}
		});

		input.setEnabled(false);
		input.add(inputAllow = new CheckboxMenuItem(BotLocale.ALLOW));
		inputAllow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				setInputEnabled(true);
			}
		});
		input.add(inputBlock = new CheckboxMenuItem(BotLocale.BLOCK));
		inputBlock.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				setInputEnabled(false);
			}
		});

		if (Configuration.OS != Configuration.OperatingSystem.MAC) {
			final MenuItem about = new MenuItem(BotLocale.ABOUT);
			help.add(about);
			about.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					showAbout();
				}
			});
		}

		final MenuItem license = new MenuItem(BotLocale.LICENSE);
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
		add(input);
		add(help);
	}

	public void update() {
		view.removeAll();
		final Bot bot = launcher.bot.get();
		if (bot != null) {
			final boolean os = bot instanceof org.powerbot.bot.rt4.Bot;
			if (os) {
				new RT4BotMenuView(launcher, view);
			} else {
				new RT6BotMenuView(launcher, view);
			}
		}
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
		JOptionPane.showMessageDialog(launcher.window.get(), text, BotLocale.ABOUT, JOptionPane.PLAIN_MESSAGE);
		GoogleAnalytics.getInstance().pageview("about/", BotLocale.ABOUT);
	}

	public void showLicense() {
		BotLauncher.openURL(Configuration.URLs.LICENSE);
		GoogleAnalytics.getInstance().pageview("license/", BotLocale.LICENSE);
	}

	public synchronized void scriptPlayPause() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				final Bot bot = launcher.bot.get();
				final ScriptController c = (ScriptController) launcher.bot.get().ctx.controller;

				if (c.valid()) {
					if (c.isSuspended()) {
						c.resume();
					} else {
						c.suspend();
					}
				} else {
					if (bot.ctx.client() != null) {
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								new BotPreferences(launcher);
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
				launcher.bot.get().ctx.controller.stop();
			}
		}).start();
	}

	public void setInputEnabled(final boolean e) {
		final SelectiveEventQueue eq = SelectiveEventQueue.getInstance();
		eq.setBlocking(!e);
		final InputSimulator s = eq.getEngine();
		if (s != null) {
			s.focus();
		}

		inputAllow.setState(!eq.isBlocking());
		inputBlock.setState(!inputAllow.getState());
	}
}
