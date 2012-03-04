package org.powerbot.gui.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.powerbot.gui.BotChrome;

public final class BotMenu extends JPopupMenu implements ActionListener {
	private static final long serialVersionUID = 1L;
	public final BotToolBar parent;

	public BotMenu(final BotToolBar parent) {
		this.parent = parent;

		final int tabs = parent.getTabCount();

		final JMenuItem newtab = new JMenuItem(Locale.NEWTAB);
		newtab.setEnabled(BotChrome.MAX_BOTS - tabs > 0);
		newtab.addActionListener(this);
		final JMenuItem closetab = new JMenuItem(Locale.CLOSETAB);
		closetab.setEnabled(tabs > 0);
		closetab.addActionListener(this);
		add(newtab);
		add(closetab);

		addSeparator();

		final JMenuItem exit = new JMenuItem(Locale.EXIT);
		exit.addActionListener(this);
		add(exit);
	}

	@Override
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
