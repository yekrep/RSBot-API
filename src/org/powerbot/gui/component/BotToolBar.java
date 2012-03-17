package org.powerbot.gui.component;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.border.EmptyBorder;

import org.powerbot.game.bot.Bot;
import org.powerbot.gui.BotChrome;
import org.powerbot.util.io.Resources;

/**
 * @author Paris
 */
public final class BotToolBar extends JToolBar {
	private static final long serialVersionUID = 1L;
	public final BotChrome parent;
	private final JButton tabAdd, tabDelete, scriptPlay, scriptStop;

	public BotToolBar(final BotChrome parent) {
		this.parent = parent;
		setFloatable(false);
		setBorder(new EmptyBorder(1, 3, 1, 3));

		tabDelete = new JButton(new ImageIcon(Resources.getImage(Resources.Paths.TAB_DELETE)));
		tabDelete.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent arg0) {
				final int tab = getOpenedTab();
				if (tab != -1) {
					closeTab(tab);
				}
			}
		});
		tabDelete.setToolTipText(BotLocale.CLOSETAB);
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
		tabAdd.setToolTipText(BotLocale.NEWTAB);
		tabAdd.setFocusable(false);
		add(tabAdd);

		add(Box.createHorizontalGlue());

		scriptPlay = new JButton(new ImageIcon(Resources.getImage(Resources.Paths.CONTROL_PLAY)));
		scriptPlay.setToolTipText(BotLocale.PLAYSCRIPT);
		scriptPlay.setFocusable(false);
		scriptPlay.setVisible(false);
		add(scriptPlay);
		scriptStop = new JButton(new ImageIcon(Resources.getImage(Resources.Paths.CONTROL_STOP)));
		scriptStop.setToolTipText(BotLocale.STOPSCRIPT);
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
		activateTab(n);
		tabAdd.setVisible(BotChrome.MAX_BOTS - Bot.bots.size() > 0);
		BotChrome.panel.setBot(bot);
		new Thread(bot.threadGroup, bot).start();
	}

	public void closeTab(final int n) {
		final List<Bot> bots = Collections.unmodifiableList(Bot.bots);
		boolean loggedIn = false;
		if (n > 0 && n < bots.size()) {
			final Bot bot = bots.get(n);
			final int state = bot.client.getLoginIndex() * bot.multipliers.GLOBAL_LOGININDEX;
			loggedIn = state == bot.constants.CLIENTSTATE_11 || state == bot.constants.CLIENTSTATE_12;
		}
		final BotButton b = getTabButton(n);
		if (b == null) {
			return;
		}
		try {
			if (loggedIn) {
				if (JOptionPane.showConfirmDialog(parent, "Are you sure you want to close this tab?", BotLocale.CLOSETAB, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) != JOptionPane.YES_OPTION) {
					return;
				}
			}
		} catch (final RuntimeException ignored) {
		}

		remove(n + 1);
		final boolean a = getTabCount() > 0;
		tabDelete.setVisible(a);
		if (a) {
			final int x = n == 0 ? 1 : n - 1;
			activateTab(x);
		} else {
			BotChrome.panel.setBot(null);
		}
		b.getBot().killEnvironment();
		BotChrome.panel.repaint();
		if (getTabCount() == 0) {
			Logger.getLogger(Bot.class.getName()).log(Level.INFO, "Add a tab to start another bot", "Closed");
		}
		System.gc();
	}

	private void activateTab(final int n) {
		if (getActiveTab() == n) {
			return;
		}
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

	public int getActiveTab() {
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
