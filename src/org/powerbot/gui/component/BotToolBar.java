package org.powerbot.gui.component;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.border.EmptyBorder;

import org.powerbot.util.io.Resources;

public final class BotToolBar extends JToolBar {
	private static final long serialVersionUID = 1L;

	public BotToolBar() {
		setBorder(new EmptyBorder(1, 3, 1, 3));
		add(new BotButton("Game"));
		add(Box.createHorizontalGlue());
		final JButton settings = new JButton(new ImageIcon(Resources.getImage(Resources.Paths.COG)));
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
