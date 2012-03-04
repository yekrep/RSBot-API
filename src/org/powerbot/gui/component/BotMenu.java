package org.powerbot.gui.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import org.powerbot.gui.BotChrome;
import org.powerbot.util.Configuration;
import org.powerbot.util.io.Resources;

public final class BotMenu extends JPopupMenu implements ActionListener {
	private static final long serialVersionUID = 1L;
	public final BotToolBar parent;

	public BotMenu(final BotToolBar parent) {
		this.parent = parent;

		final int tabs = parent.getTabCount();

		final JMenuItem newtab = new JMenuItem(Locale.NEWTAB);
		newtab.setIcon(new ImageIcon(Resources.getImage(Resources.Paths.TAB_ADD)));
		newtab.setEnabled(BotChrome.MAX_BOTS - tabs > 0);
		newtab.addActionListener(this);
		final JMenuItem closetab = new JMenuItem(Locale.CLOSETAB);
		closetab.setIcon(new ImageIcon(Resources.getImage(Resources.Paths.TAB_DELETE)));
		closetab.setEnabled(tabs > 0);
		closetab.addActionListener(this);
		add(newtab);
		add(closetab);
		addSeparator();

		final JMenuItem accounts = new JMenuItem(Locale.ACCOUNTS);
		accounts.setIcon(new ImageIcon(Resources.getImage(Resources.Paths.REPORT_KEY)));
		add(accounts);
		addSeparator();

		final JMenuItem site = new JMenuItem(Locale.POWERBOT);
		site.setIcon(new ImageIcon(Resources.getImage(Resources.Paths.ICON_SMALL)));
		site.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent arg0) {
				BotChrome.openURL(Configuration.URLs.SITE);
			}
		});
		add(site);
		final JMenuItem about = new JMenuItem(Locale.ABOUT);
		about.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent arg0) {
				JOptionPane.showMessageDialog(parent.parent, new String[] {
						Locale.COPYRIGHT,
						"Unauthorised use of this application is prohibited.\n\n",
						"RuneScape\u00ae is a trademark of Jagex \u00a9 1999 - 2011 Jagex, Ltd.",
						"RuneScape content and materials are trademarks and copyrights of Jagex or its licensees.",
						"This program is issued with no warranty and is not affiliated with Jagex Ltd., nor do they endorse usage of our software.\n\n",
						"Visit " + Configuration.URLs.SITE + "/ for more information."},
						Locale.ABOUT,
						JOptionPane.INFORMATION_MESSAGE);
			}
		});
		add(about);
		addSeparator();

		final JMenuItem exit = new JMenuItem(Locale.EXIT);
		exit.addActionListener(this);
		add(exit);
	}

	public void actionPerformed(final ActionEvent e) {
		final String a = e.getActionCommand();
		if (a.equals(Locale.NEWTAB)) {
			parent.addTab();
		} else if (a.equals(Locale.CLOSETAB)) {
			parent.closeTab(parent.getOpenedTab());
		} else if (a.equals(Locale.EXIT)) {
			parent.parent.dispatchEvent(new WindowEvent(parent.parent, WindowEvent.WINDOW_CLOSING));
		}
	}
}
