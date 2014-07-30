package org.powerbot.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
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
import org.powerbot.bot.AbstractBot;
import org.powerbot.bot.ScriptController;
import org.powerbot.misc.GoogleAnalytics;
import org.powerbot.misc.ScriptBundle;
import org.powerbot.script.Bot;
import org.powerbot.script.BotMenuListener;
import org.powerbot.script.Script;

class BotMenuBar extends JMenuBar {
	private static final long serialVersionUID = -4186554435386744949L;
	private final BotChrome chrome;
	private final JMenuItem play, stop;

	public BotMenuBar(final BotChrome chrome) {
		this.chrome = chrome;

		final JMenu file = new JMenu(BotLocale.FILE), edit = new JMenu(BotLocale.EDIT), view = new JMenu(BotLocale.VIEW),
				input = new JMenu(BotLocale.INPUT), help = new JMenu(BotLocale.HELP);

		final JMenuItem newTab = new JMenuItem(BotLocale.NEW_WINDOW);
		file.add(newTab);
		newTab.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				Boot.fork();
			}
		});
		final JMenuItem closeTab = new JMenuItem(BotLocale.CLOSE_WINDOW);
		file.add(closeTab);
		closeTab.setVisible(false);
		closeTab.setEnabled(false);
		closeTab.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				final AbstractBot b = chrome.bot.get();
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

		file.addMenuListener(new MenuListener() {
			@Override
			public void menuSelected(final MenuEvent e) {
				closeTab.setEnabled(chrome.bot.get() != null && !chrome.bot.get().pending.get());
			}

			@Override
			public void menuDeselected(final MenuEvent e) {
			}

			@Override
			public void menuCanceled(final MenuEvent e) {
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

		view.addMenuListener(new MenuListener() {
			@Override
			public void menuSelected(final MenuEvent e) {
				final JMenu menu = (JMenu) e.getSource();
				menu.removeAll();
				final Bot bot = chrome.bot.get();
				if (bot != null) {
					final boolean os = bot instanceof org.powerbot.bot.rt4.Bot;
					if (os) {
						new RT4BotMenuView(chrome, menu);
					} else {
						new RT6BotMenuView(chrome, menu);
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

		final ImageIcon[] playIcons = new ImageIcon[]{createControlIcon(1), createControlIcon(2)};
		play = new JMenuItem(BotLocale.SCRIPT_PLAY);
		play.setIcon(playIcons[0]);
		edit.add(play);
		play.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				scriptPlayPause();
			}
		});
		stop = new JMenuItem(BotLocale.SCRIPT_STOP);
		stop.setIcon(createControlIcon(0));
		edit.add(stop);
		stop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				scriptStop();
			}
		});

		edit.addSeparator();
		final JMenu options = new JMenu(BotLocale.OPTIONS);
		edit.add(options);

		options.addMenuListener(new MenuListener() {
			@Override
			public void menuSelected(final MenuEvent e) {
				final JMenu m = (JMenu) e.getSource();
				m.removeAll();

				final ScriptController c = chrome.bot.get() == null ? null : (ScriptController) chrome.bot.get().ctx.controller;
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
				final ScriptController c = chrome.bot.get() == null ? null : (ScriptController) chrome.bot.get().ctx.controller;
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
				final ScriptController c = chrome.bot.get() == null ? null : (ScriptController) chrome.bot.get().ctx.controller;
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
				final ScriptController c = chrome.bot.get() == null ? null : (ScriptController) chrome.bot.get().ctx.controller;
				final boolean active = c != null && c.valid() && !c.isStopping(), running = active && !c.isSuspended();

				play.setEnabled(chrome.bot.get() != null && chrome.bot.get().ctx.client() != null && !BotPreferences.loading.get());
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
				if (chrome.bot.get() != null) {
					new BotMenuInput(menu, chrome.bot.get().ctx.input);
				}
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
		JOptionPane.showMessageDialog(chrome, text, BotLocale.ABOUT, JOptionPane.PLAIN_MESSAGE);
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
				final Bot bot = chrome.bot.get();
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
}
