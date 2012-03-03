package org.powerbot.gui.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.powerbot.gui.Chrome;
import org.powerbot.util.io.Resources;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public final class BotToolBar extends JToolBar {
	private static final long serialVersionUID = 1L;
	public final Chrome parent;
	private final BotMenu menu;

	public BotToolBar(final Chrome parent) {
		this.parent = parent;
		setBorder(new EmptyBorder(1, 3, 1, 3));
		add(new BotButton("Game"));
		add(Box.createHorizontalGlue());
		menu = new BotMenu(this);
		final JButton settings = new JButton(new ImageIcon(Resources.getImage(Resources.Paths.COG)));
		settings.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				menu.show(settings, settings.getWidth() / 2, settings.getHeight() / 2);
			}
		});
		settings.setFocusable(false);
		add(settings);
	}

	private final class BotButton extends JButton {
		private static final long serialVersionUID = 1L;

		public BotButton(final String name) {
			super(name);
			setFocusable(false);
		}
	}
}
