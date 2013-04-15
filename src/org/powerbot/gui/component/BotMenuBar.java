package org.powerbot.gui.component;

import org.powerbot.bot.Bot;
import org.powerbot.gui.BotChrome;
import org.powerbot.gui.controller.BotInteract;
import org.powerbot.script.internal.ScriptManager;
import org.powerbot.service.NetworkAccount;
import org.powerbot.util.Configuration;
import org.powerbot.util.io.Resources;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Paris
 */
public class BotMenuBar extends JMenuBar implements ActionListener {
	private final JMenuItem signin, play, stop;
	private final JCheckBoxMenuItem logpane;

	public BotMenuBar() {
		final JMenu file = new JMenu(BotLocale.FILE), edit = new JMenu(BotLocale.EDIT), view = new JMenu(BotLocale.VIEW),
				script = new JMenu(BotLocale.SCRIPTS), input = new JMenu(BotLocale.INPUT), help = new JMenu(BotLocale.HELP);

		file.add(item(BotLocale.NEWTAB));
		if (Configuration.OS != Configuration.OperatingSystem.MAC) {
			file.addSeparator();
			file.add(item(BotLocale.EXIT));
		}

		signin = item(BotLocale.SIGNIN);
		edit.add(signin);
		edit.add(item(BotLocale.ACCOUNTS));

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

		logpane = new JCheckBoxMenuItem(BotLocale.LOGPANE);
		logpane.addActionListener(this);

		view.addMenuListener(new MenuListener() {
			@Override
			public void menuSelected(final MenuEvent e) {
				final JMenu menu = (JMenu) e.getSource();
				menu.removeAll();
				menu.add(logpane);
				if (Bot.instantiated()) {
					new BotMenuView(menu);
				}
			}

			@Override
			public void menuDeselected(final MenuEvent e) {
			}

			@Override
			public void menuCanceled(final MenuEvent e) {
			}
		});

		final ImageIcon[] playIcons = new ImageIcon[] { new ImageIcon(Resources.getImage(Resources.Paths.PLAY)), new ImageIcon(Resources.getImage(Resources.Paths.PAUSE)) };
		play = item(BotLocale.PLAYSCRIPT);
		play.setIcon(playIcons[0]);
		script.add(play);
		stop = item(BotLocale.STOPSCRIPT);
		stop.setIcon(new ImageIcon(Resources.getImage(Resources.Paths.STOP)));
		script.add(stop);

		script.addMenuListener(new MenuListener() {
			@Override
			public void menuSelected(final MenuEvent e) {
				final boolean b = Bot.instantiated();
				final ScriptManager container = b ? Bot.getInstance().getScriptController() : null;
				final boolean active = container != null && !container.getScripts().isEmpty() && !container.isStopping(), running = active && !container.isSuspended();
				play.setText(running ? BotLocale.PAUSESCRIPT : active ? BotLocale.RESUMESCRIPT : BotLocale.PLAYSCRIPT);
				play.setIcon(playIcons[running ? 1 : 0]);
				stop.setEnabled(active);
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
				if (Bot.instantiated()) {
					new BotMenuInput(menu);
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
			help.add(item(BotLocale.ABOUT));
		}
		help.add(item(BotLocale.LICENSE));
		help.add(item(BotLocale.WEBSITE));

		add(file);
		add(edit);
		add(view);
		add(script);
		add(input);
		add(help);
	}

	private JMenuItem item(final String s) {
		final JMenuItem item = new JMenuItem(s);
		item.addActionListener(this);
		return item;
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		String s = e.getActionCommand();
		if (e.getSource() == signin) {
			s = BotLocale.SIGNIN;
		}
		switch (s) {
			case BotLocale.NEWTAB: BotInteract.tabAdd(); break;
			case BotLocale.EXIT: BotInteract.tabClose(false); break;
			case BotLocale.SIGNIN: BotInteract.showDialog(BotInteract.Action.SIGNIN); break;
			case BotLocale.LOGPANE: logpane.setState(BotInteract.toggleLogPane()); break;
			case BotLocale.ACCOUNTS: BotInteract.showDialog(BotInteract.Action.ACCOUNTS); break;
			case BotLocale.PLAYSCRIPT: case BotLocale.PAUSESCRIPT: case BotLocale.RESUMESCRIPT: BotInteract.scriptPlayPause(); break;
			case BotLocale.STOPSCRIPT: BotInteract.scriptStop(); break;
			case BotLocale.ABOUT: BotInteract.showDialog(BotInteract.Action.ABOUT); break;
			case BotLocale.LICENSE: BotInteract.showDialog(BotInteract.Action.LICENSE); break;
			case BotLocale.WEBSITE: BotChrome.openURL(Configuration.URLs.SITE); break;
		}
	}
}
