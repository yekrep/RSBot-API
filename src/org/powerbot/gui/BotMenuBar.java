package org.powerbot.gui;

import java.awt.CheckboxMenuItem;
import java.awt.Font;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.net.MalformedURLException;
import java.util.Calendar;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.powerbot.Boot;
import org.powerbot.Configuration;
import org.powerbot.bot.AbstractBot;
import org.powerbot.bot.ScriptController;
import org.powerbot.misc.GoogleAnalytics;
import org.powerbot.misc.ScriptBundle;
import org.powerbot.script.AbstractScript;
import org.powerbot.script.BotMenuActionListener;
import org.powerbot.script.Script;

public class BotMenuBar extends MenuBar {
	private static final long serialVersionUID = -4186554435386744949L;
	private final BotChrome chrome;
	private final Menu view;
	private final MenuItem play, stop, options;
	private final CheckboxMenuItem inputAllow, inputBlock;

	public BotMenuBar(final BotChrome chrome) {
		this.chrome = chrome;

		final Menu file = new Menu(BotLocale.FILE), edit = new Menu(BotLocale.EDIT),
				input = new Menu(BotLocale.INPUT), help = new Menu(BotLocale.HELP);

		view = new Menu(BotLocale.VIEW);

		final MenuItem newRS3 = new MenuItem(BotLocale.NEW + "RS3");
		file.add(newRS3);
		newRS3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				System.setProperty("com.jagex.config",
						((MenuItem) e.getSource()).getLabel().substring(BotLocale.NEW.length()).toLowerCase());
				Boot.fork();
			}
		});
		final MenuItem newOS = new MenuItem(BotLocale.NEW + "OS");
		file.add(newOS);
		newOS.addActionListener(newRS3.getActionListeners()[0]);

		if (Configuration.OS != Configuration.OperatingSystem.MAC) {
			file.addSeparator();
			final MenuItem exit = new MenuItem(BotLocale.EXIT);
			file.add(exit);
			exit.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					chrome.close();
				}
			});
		}

		play = new MenuItem(BotLocale.SCRIPT_PLAY);
		play.setEnabled(false);
		edit.add(play);
		play.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				scriptPlayPause();
			}
		});
		stop = new MenuItem(BotLocale.SCRIPT_STOP);
		stop.setEnabled(false);
		edit.add(stop);
		stop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				scriptStop();
			}
		});
		edit.addSeparator();
		options = new MenuItem(BotLocale.OPTIONS);
		options.setEnabled(false);
		edit.add(options);
		options.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				final ScriptController c = chrome.bot.get() == null ? null : (ScriptController) chrome.bot.get().ctx.controller;
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

		input.add(inputAllow = new CheckboxMenuItem(BotLocale.ALLOW));
		inputAllow.setEnabled(false);
		inputAllow.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(final ItemEvent e) {
				inputSetEnabled(e.getStateChange() == ItemEvent.SELECTED);
			}
		});
		input.add(inputBlock = new CheckboxMenuItem(BotLocale.BLOCK));
		inputBlock.setEnabled(inputAllow.isEnabled());
		inputBlock.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(final ItemEvent e) {
				inputSetEnabled(e.getStateChange() != ItemEvent.SELECTED);
			}
		});

		final File logfile = new File(System.getProperty("chrome.log", ""));
		System.clearProperty("chrome.log");
		final MenuItem log = new MenuItem(BotLocale.VIEW_LOG);
		log.setEnabled(logfile.isFile());
		log.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				if (logfile.isFile()) {
					try {
						BotChrome.openURL(logfile.toURI().toURL().toString());
					} catch (final MalformedURLException ignored) {
					}
				}
			}
		});
		help.add(log);
		help.addSeparator();

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

		final MenuItem support = new MenuItem(BotLocale.SUPPORT);
		help.add(support);
		support.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				BotChrome.openURL(Configuration.URLs.SUPPORT);
			}
		});

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
		final AbstractBot bot = chrome.bot.get();
		final boolean e = bot != null, h = e && bot.ctx.client() != null;

		if (e) {
			view.removeAll();
			inputUpdate(bot.ctx.input.blocking());

			if (h) {
				final boolean os = bot instanceof org.powerbot.bot.rt4.Bot;
				if (os) {
					new RT4BotMenuView(chrome, view);
				} else {
					new RT6BotMenuView(chrome, view);
				}
			}
		}

		play.setEnabled(e);
		inputAllow.setEnabled(e);
		inputBlock.setEnabled(inputAllow.isEnabled());
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
		JOptionPane.showMessageDialog(chrome.window.get(), text, BotLocale.ABOUT, JOptionPane.PLAIN_MESSAGE);
		GoogleAnalytics.getInstance().pageview("about/", BotLocale.ABOUT);
	}

	void showLicense() {
		BotChrome.openURL(Configuration.URLs.LICENSE);
		GoogleAnalytics.getInstance().pageview("license/", BotLocale.LICENSE);
	}

	void scriptPlayPause() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				final AbstractBot bot = chrome.bot.get();
				final ScriptController c = (ScriptController) chrome.bot.get().ctx.controller;

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
								new BotPreferences(chrome);
							}
						});
					}
				}
			}
		}).start();
	}

	public void scriptStop() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				chrome.bot.get().ctx.controller.stop();
			}
		}).start();
	}

	public void scriptUpdate() {
		final ScriptController c = chrome.bot.get() == null ? null : (ScriptController) chrome.bot.get().ctx.controller;
		final boolean active = c != null && c.valid() && !c.isStopping(), running = active && !c.isSuspended();

		play.setEnabled(chrome.bot.get() != null && chrome.bot.get().ctx.client() != null && !BotPreferences.loading.get());
		play.setLabel(running ? BotLocale.SCRIPT_PAUSE : active ? BotLocale.SCRIPT_RESUME : BotLocale.SCRIPT_PLAY);
		stop.setEnabled(active);

		if (active) {
			final AbstractScript s = c.script();
			options.setEnabled(s != null && s instanceof BotMenuActionListener);
		} else {
			options.setEnabled(false);
		}
	}

	public void inputSetEnabled(final boolean e) {
		chrome.bot.get().ctx.input.blocking(!e);
	}

	public void inputUpdate(final boolean b) {
		inputAllow.setState(!b);
		inputBlock.setState(!inputAllow.getState());
	}
}
