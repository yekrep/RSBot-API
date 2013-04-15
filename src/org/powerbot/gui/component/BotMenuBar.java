package org.powerbot.gui.component;

import org.powerbot.bot.Bot;
import org.powerbot.gui.BotChrome;
import org.powerbot.gui.controller.BotInteract;
import org.powerbot.service.NetworkAccount;
import org.powerbot.util.Configuration;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Paris
 */
public class BotMenuBar extends JMenuBar implements ActionListener {
	private final JMenuItem signin;
	private final JCheckBoxMenuItem logpane;

	public BotMenuBar() {
		final JMenu file = new JMenu(BotLocale.FILE), edit = new JMenu(BotLocale.EDIT),
				view = new JMenu(BotLocale.VIEW), input = new JMenu(BotLocale.INPUT), help = new JMenu(BotLocale.HELP);

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
			case BotLocale.ABOUT: BotInteract.showDialog(BotInteract.Action.ABOUT); break;
			case BotLocale.LICENSE: BotInteract.showDialog(BotInteract.Action.LICENSE); break;
			case BotLocale.WEBSITE: BotChrome.openURL(Configuration.URLs.SITE); break;
		}
	}
}
