package org.powerbot.gui.component;

import org.powerbot.game.bot.Bot;
import org.powerbot.gui.BotChrome;
import org.powerbot.util.io.Resources;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.border.EmptyBorder;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public final class BotToolBar extends JToolBar {
	private static final long serialVersionUID = 1L;
	public final BotChrome parent;
	private final BotMenu menu;
	private final Bot[] bots = new Bot[BotChrome.MAX_BOTS];
	private final JButton tabadd;

	public BotToolBar(final BotChrome parent) {
		this.parent = parent;
		setBorder(new EmptyBorder(1, 3, 1, 3));

		tabadd = new JButton(new ImageIcon(Resources.getImage(Resources.Paths.TAB_ADD)));
		tabadd.addActionListener(new ActionListener() {
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
		setTab(n);
		tabadd.setVisible(BotChrome.MAX_BOTS - Bot.bots.size() > 1);
		final Bot bot = new Bot();
		new Thread(bot).start();
		bots[n] = bot;
		BotChrome.panel.setBot(bot);
	}

	private void setTab(final int n) {
		int i = 0;
		for (final Component c : getComponents()) {
			if (c instanceof BotButton) {
				c.setFont(c.getFont().deriveFont(n == i ? Font.BOLD : 0));
			}
			i++;
		}
		if (n > 0 && n < Bot.bots.size()) {
			BotChrome.panel.setBot(Bot.bots.get(n));
		}
	}

	private final class BotButton extends JButton implements ActionListener {
		private static final long serialVersionUID = 1L;

		public BotButton(final String name) {
			super(name);
			setFocusable(false);
			addActionListener(this);
		}

		public void actionPerformed(final ActionEvent arg0) {
			setTab(getComponentIndex(this));
		}
	}
}
