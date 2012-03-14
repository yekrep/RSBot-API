package org.powerbot.gui.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import org.powerbot.gui.BotAccounts;
import org.powerbot.gui.BotChrome;
import org.powerbot.gui.BotSignin;
import org.powerbot.service.NetworkAccount;
import org.powerbot.util.Configuration;
import org.powerbot.util.io.Resources;

/**
 * @author Paris
 */
public final class BotMenu extends JPopupMenu implements ActionListener {
	private static final long serialVersionUID = 1L;
	public final BotToolBar parent;

	public BotMenu(final BotToolBar parent) {
		this.parent = parent;

		final int tabs = parent.getTabCount();

		final JMenuItem newtab = new JMenuItem(BotLocale.NEWTAB);
		newtab.setIcon(new ImageIcon(Resources.getImage(Resources.Paths.TAB_ADD)));
		newtab.setEnabled(BotChrome.MAX_BOTS - tabs > 0);
		newtab.addActionListener(this);
		final JMenuItem closetab = new JMenuItem(BotLocale.CLOSETAB);
		closetab.setIcon(new ImageIcon(Resources.getImage(Resources.Paths.TAB_DELETE)));
		closetab.setEnabled(tabs > 0);
		closetab.addActionListener(this);
		add(newtab);
		add(closetab);
		addSeparator();

		final JMenuItem accounts = new JMenuItem(BotLocale.ACCOUNTS);
		accounts.setIcon(new ImageIcon(Resources.getImage(Resources.Paths.REPORT_KEY)));
		accounts.addActionListener(this);
		add(accounts);
		addSeparator();

		final JMenuItem signin = new JMenuItem(BotLocale.SIGNIN + "...");
		if (NetworkAccount.getInstance().isLoggedIn()) {
			signin.setText(BotLocale.SIGNEDINAS + " " + NetworkAccount.getInstance().getAccount().getDisplayName());
		}
		add(signin);
		signin.addActionListener(this);
		addSeparator();

		final JMenuItem site = new JMenuItem(BotLocale.POWERBOT);
		site.setIcon(new ImageIcon(Resources.getImage(Resources.Paths.ICON_SMALL)));
		site.addActionListener(this);
		add(site);
		final JMenuItem about = new JMenuItem(BotLocale.ABOUT);
		about.setIcon(new ImageIcon(Resources.getImage(Resources.Paths.INFORMATION)));
		about.addActionListener(this);
		add(about);
		addSeparator();

		final JMenuItem exit = new JMenuItem(BotLocale.EXIT);
		exit.addActionListener(this);
		add(exit);
	}

	public void actionPerformed(final ActionEvent e) {
		final String a = e.getActionCommand();
		if (a.equals(BotLocale.NEWTAB)) {
			parent.addTab();
		} else if (a.equals(BotLocale.CLOSETAB)) {
			parent.closeTab(parent.getOpenedTab());
		} else if (a.equals(BotLocale.ACCOUNTS)) {
			new BotAccounts(parent.parent);
		} else if (a.startsWith(BotLocale.SIGNIN) || a.startsWith(BotLocale.SIGNEDINAS)) {
			new BotSignin(parent.parent);
		} else if (a.equals(BotLocale.POWERBOT)) {
			BotChrome.openURL(Resources.getServerLinks().get("site"));
		} else if (a.equals(BotLocale.ABOUT)) {
			JOptionPane.showMessageDialog(parent.parent, new String[]{
					Configuration.NAME + " (build " + Integer.toString(Configuration.VERSION) + ")\n",
					BotLocale.COPYRIGHT,
					"Unauthorised use of this application is prohibited.\n\n",
					"RuneScape\u00ae is a trademark of Jagex \u00a9 1999 - 2011 Jagex, Ltd.",
					"RuneScape content and materials are trademarks and copyrights of Jagex or its licensees.",
					"This program is issued with no warranty and is not affiliated with Jagex Ltd., nor do they endorse usage of our software.\n\n",
					"Visit " + Resources.getServerLinks().get("site") + " for more information."},
					BotLocale.ABOUT,
					JOptionPane.INFORMATION_MESSAGE);
		} else if (a.equals(BotLocale.EXIT)) {
			parent.parent.dispatchEvent(new WindowEvent(parent.parent, WindowEvent.WINDOW_CLOSING));
		}
	}
}
