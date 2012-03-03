package org.powerbot.gui.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.powerbot.game.bot.Bot;
import org.powerbot.gui.BotChrome;
import org.powerbot.util.io.Resources;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public final class BotToolBar extends JToolBar {
	private static final long serialVersionUID = 1L;
	public final BotChrome parent;
	private final BotMenu menu;
	private final JButton tabadd;

	public BotToolBar(final BotChrome parent) {
		this.parent = parent;
		setBorder(new EmptyBorder(1, 3, 1, 3));

		tabadd = new JButton(new ImageIcon(Resources.getImage(Resources.Paths.TAB_ADD)));
		tabadd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				if (Bot.bots.size() < BotChrome.MAX_BOTS) {
					addBot();
				}
			}
		});
		tabadd.setToolTipText("New tab");
		tabadd.setFocusable(false);
		add(tabadd);

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

	private void addBot() {
		final int n = Bot.bots.size();
		add(new BotButton("Game " + (n + 1)), n);
		tabadd.setVisible(BotChrome.MAX_BOTS - Bot.bots.size() > 1);
		final Bot bot = new Bot();
		new Thread(bot).start();
		BotChrome.panel.setBot(bot);
	}

	private final class BotButton extends JButton {
		private static final long serialVersionUID = 1L;

		public BotButton(final String name) {
			super(name);
			setFocusable(false);
		}
	}
}
