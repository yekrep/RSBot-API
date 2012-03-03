package org.powerbot.gui.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public final class BotMenu extends JPopupMenu implements ActionListener {
	private static final long serialVersionUID = 1L;
	public final BotToolBar parent;

	public BotMenu(final BotToolBar parent) {
		this.parent = parent;
		addSeparator();
		
		final JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(this);
		add(exit);
	}

	public void actionPerformed(final ActionEvent e) {
		final String a = e.getActionCommand();
		if (a.equals("Exit")) {
			parent.parent.dispatchEvent(new WindowEvent(parent.parent, WindowEvent.WINDOW_CLOSING));
		}
	}
}
