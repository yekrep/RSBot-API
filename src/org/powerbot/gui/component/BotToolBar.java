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
	private final JButton tabAdd, tabDelete, scriptPlay, scriptStop;

	public BotToolBar(final BotChrome parent) {
		this.parent = parent;
		setBorder(new EmptyBorder(1, 3, 1, 3));

		tabDelete = new JButton(new ImageIcon(Resources.getImage(Resources.Paths.TAB_DELETE)));
		tabDelete.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent arg0) {
				closeTab(getOpenedTab());
			}
		});
		tabDelete.setToolTipText(Locale.CLOSETAB);
		tabDelete.setFocusable(false);
		tabDelete.setVisible(false);
		add(tabDelete);

		tabAdd = new JButton(new ImageIcon(Resources.getImage(Resources.Paths.TAB_ADD)));
		tabAdd.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if (Bot.bots.size() < BotChrome.MAX_BOTS) {
					addTab();
				}
			}
		});
		tabAdd.setToolTipText(Locale.NEWTAB);
		tabAdd.setFocusable(false);
		add(tabAdd);

		add(Box.createHorizontalGlue());

		scriptPlay = new JButton(new ImageIcon(Resources.getImage(Resources.Paths.CONTROL_PLAY)));
		scriptPlay.setToolTipText(Locale.PLAYSCRIPT);
		scriptPlay.setFocusable(false);
		scriptPlay.setVisible(false);
		add(scriptPlay);
		scriptStop = new JButton(new ImageIcon(Resources.getImage(Resources.Paths.CONTROL_STOP)));
		scriptStop.setToolTipText(Locale.STOPSCRIPT);
		scriptStop.setFocusable(false);
		scriptStop.setVisible(false);
		add(scriptStop);
		add(Box.createHorizontalStrut(16));

		final BotToolBar t = this;
		final JButton settings = new JButton(new ImageIcon(Resources.getImage(Resources.Paths.WRENCH)));
		settings.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent arg0) {
				new BotMenu(t).show(settings, settings.getWidth() / 2, settings.getHeight() / 2);
			}
		});
		settings.setFocusable(false);
		add(settings);
	}

	public void addTab() {
		final int n = Bot.bots.size(), x = n + 1;
		final Bot bot = new Bot();
		add(new BotButton("Game", bot), x);
		tabDelete.setVisible(true);
		openTab(n);
		tabAdd.setVisible(BotChrome.MAX_BOTS - Bot.bots.size() > 0);
		new Thread(bot).start();
		BotChrome.panel.setBot(bot);
	}

	public void closeTab(final int n) {
		final BotButton b = getTabButton(n);
		if (b == null) {
			return;
		}
		remove(n + 1);
		final boolean a = getTabCount() > 0;
		tabDelete.setVisible(a);
		if (a) {
			final int x = n == 0 ? 1 : n - 1;
			openTab(x);
		} else {
			BotChrome.panel.setBot(null);
		}
		b.getBot().killEnvironment();
		BotChrome.panel.repaint();
	}

	private void openTab(final int n) {
		int i = 0;
		for (final Component c : getComponents()) {
			if (c instanceof BotButton) {
				final boolean a = n == i;
				c.setFont(c.getFont().deriveFont(a ? Font.BOLD : 0));
				if (a) {
					BotChrome.panel.setBot(((BotButton) c).getBot());
				}
				i++;
			}
		}
	}

	public int getOpenedTab() {
		int i = 0;
		for (final Component c : getComponents()) {
			if (c instanceof BotButton) {
				final boolean bold = (c.getFont().getStyle() & Font.BOLD) == Font.BOLD;
				if (bold) {
					return i;
				}
				i++;
			}
		}
		return -1;
	}

	private BotButton getTabButton(final int n) {
		int i = 0;
		for (final Component c : getComponents()) {
			if (c instanceof BotButton) {
				if (i == n) {
					return (BotButton) c;
				}
				i++;
			}
		}
		return null;
	}

	public int getTabCount() {
		int i = 0;
		for (final Component c : getComponents()) {
			if (c instanceof BotButton) {
				i++;
			}
		}
		return i;
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

		public void actionPerformed(final ActionEvent arg0) {
			openTab(getComponentIndex(this) - 1);
		}

		public Bot getBot() {
			return bot;
		}
	}
}
