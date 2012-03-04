package org.powerbot.gui.component;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.border.EmptyBorder;

import org.powerbot.game.bot.Bot;
import org.powerbot.gui.BotChrome;
import org.powerbot.util.io.Resources;

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
			public void actionPerformed(final ActionEvent arg0) {
				menu.show(settings, settings.getWidth() / 2, settings.getHeight() / 2);
			}
		});
		settings.setFocusable(false);
		add(settings);
	}

	private void addBot() {
		final int n = Bot.bots.size();
		final Bot bot = new Bot();
		add(new BotButton("Game " + (n + 1), bot), n);
		setTab(n);
		tabadd.setVisible(BotChrome.MAX_BOTS - Bot.bots.size() > 1);
		new Thread(bot).start();
		BotChrome.panel.setBot(bot);
	}

	private void setTab(final int n) {
		int i = 0;
		for (final Component c : getComponents()) {
			if (c instanceof BotButton) {
				c.setFont(c.getFont().deriveFont(n == i ? Font.BOLD : 0));
				BotChrome.panel.setBot(((BotButton) c).getBot());
			}
			i++;
		}
	}

	private final class BotButton extends JButton implements ActionListener {
		private static final long serialVersionUID = 1L;
		private final Bot bot;

		public BotButton(final String name, final Bot bot) {
			super(name);
			this.bot = bot;
			setFocusable(false);
			addActionListener(this);
		}

		@Override
		public void actionPerformed(final ActionEvent arg0) {
			setTab(getComponentIndex(this));
		}

		public Bot getBot() {
			return bot;
		}
	}
}
