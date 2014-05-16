package org.powerbot.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Calendar;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.powerbot.Boot;
import org.powerbot.Configuration;
import org.powerbot.bot.ScriptController;
import org.powerbot.misc.GoogleAnalytics;
import org.powerbot.script.Bot;

class BotMenuBar extends MenuBar {
	private static final long serialVersionUID = -4186554435386744949L;
	private final BotLauncher launcher;
	private final MenuItem play, stop;

	public BotMenuBar(final BotLauncher launcher) {
		this.launcher = launcher;

		final Menu file = new Menu(BotLocale.FILE), edit = new Menu(BotLocale.EDIT), view = new Menu(BotLocale.VIEW),
				input = new Menu(BotLocale.INPUT), help = new Menu(BotLocale.HELP);

		final MenuItem newtab = new MenuItem(BotLocale.NEW_WINDOW);
		file.add(newtab);
		newtab.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				Boot.fork();
			}
		});
		final MenuItem closeTab = new MenuItem(BotLocale.CLOSE_WINDOW);
		file.add(closeTab);
		//closeTab.setVisible(false);
		closeTab.setEnabled(false);
		closeTab.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				final Bot b = launcher.bot.get();
				if (b != null) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							b.close();
						}
					}).start();
				}
			}
		});

		/*
		file.addMenuListener(new MenuListener() {
			@Override
			public void menuSelected(final MenuEvent e) {
				closeTab.setEnabled(launcher.bot.get() != null && !launcher.bot.get().pending.get());
			}

			@Override
			public void menuDeselected(final MenuEvent e) {
			}

			@Override
			public void menuCanceled(final MenuEvent e) {
			}
		});
		*/

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

		/*
		view.addMenuListener(new MenuListener() {
			@Override
			public void menuSelected(final MenuEvent e) {
				final JMenu menu = (JMenu) e.getSource();
				menu.removeAll();
				final Bot bot = launcher.bot.get();
				if (bot != null) {
					final boolean os = bot instanceof org.powerbot.bot.rt4.Bot;
					if (os) {
						new RT4BotMenuView(launcher, menu);
					} else {
						new RT6BotMenuView(launcher, menu);
					}
				}
			}

			@Override
			public void menuDeselected(final MenuEvent e) {
			}

			@Override
			public void menuCanceled(final MenuEvent e) {
			}
		});
		*/

		final ImageIcon[] playIcons = new ImageIcon[]{createControlIcon(1), createControlIcon(2)};
		play = new MenuItem(BotLocale.SCRIPT_PLAY);
		//play.setIcon(playIcons[0]);
		edit.add(play);
		play.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				scriptPlayPause();
			}
		});
		stop = new MenuItem(BotLocale.SCRIPT_STOP);
		//stop.setIcon(createControlIcon(0));
		edit.add(stop);
		stop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				scriptStop();
			}
		});

		edit.addSeparator();
		final Menu options = new Menu(BotLocale.OPTIONS);
		edit.add(options);

		/*
		options.addMenuListener(new MenuListener() {
			@Override
			public void menuSelected(final MenuEvent e) {
				final JMenu m = (JMenu) e.getSource();
				m.removeAll();

				final ScriptController c = launcher.bot.get() == null ? null : (ScriptController) launcher.bot.get().ctx.controller;
				if (c == null || !c.valid()) {
					return;
				}

				final Script s = ((ScriptBundle) c.bundle.get()).instance.get();
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
				final ScriptController c = launcher.bot.get() == null ? null : (ScriptController) launcher.bot.get().ctx.controller;
				if (c == null || !c.valid()) {
					return;
				}

				final Script s = ((ScriptBundle) c.bundle.get()).instance.get();
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
				final ScriptController c = launcher.bot.get() == null ? null : (ScriptController) launcher.bot.get().ctx.controller;
				if (c == null || !c.valid()) {
					return;
				}

				final Script s = ((ScriptBundle) c.bundle.get()).instance.get();
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

		edit.addMenuListener(new MenuListener() {
			@Override
			public void menuSelected(final MenuEvent e) {
				final ScriptController c = launcher.bot.get() == null ? null : (ScriptController) launcher.bot.get().ctx.controller;
				final boolean active = c != null && c.valid() && !c.isStopping(), running = active && !c.isSuspended();

				play.setEnabled(launcher.bot.get() != null && launcher.bot.get().ctx.client() != null && !BotPreferences.loading.get());
				play.setText(running ? BotLocale.SCRIPT_PAUSE : active ? BotLocale.SCRIPT_RESUME : BotLocale.SCRIPT_PLAY);
				play.setIcon(playIcons[running ? 1 : 0]);
				stop.setEnabled(active);

				if (active) {
					final Script script = ((ScriptBundle) c.bundle.get()).instance.get();
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
		*/

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

	private ImageIcon createControlIcon(final int s) {
		final Image img = new BufferedImage(16, 16, BufferedImage.TYPE_4BYTE_ABGR);
		final Graphics2D g2 = (Graphics2D) img.getGraphics();
		g2.setColor(Color.BLACK);

		switch (s) {
		case 0:
			g2.fillRect(1, 1, 14, 14);
			break;
		case 1:
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			final Polygon p = new Polygon();
			p.addPoint(1, 1);
			p.addPoint(14, 8);
			p.addPoint(1, 14);
			g2.fillPolygon(p);
			break;
		case 2:
			g2.fillRect(2, 1, 5, 14);
			g2.fillRect(16 - 2 - 5, 1, 5, 14);
			break;
		}

		return new ImageIcon(img);
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
}
