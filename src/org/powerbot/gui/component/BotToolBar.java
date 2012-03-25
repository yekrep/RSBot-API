package org.powerbot.gui.component;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.powerbot.game.bot.Bot;
import org.powerbot.gui.BotChrome;
import org.powerbot.gui.BotScripts;
import org.powerbot.service.NetworkAccount;
import org.powerbot.util.io.Resources;

/**
 * @author Paris
 */
public final class BotToolBar extends JToolBar implements ActionListener {
	private static final long serialVersionUID = 1L;
	public final BotChrome parent;
	private final JButton tabAdd, scriptPlay, scriptStop;
	private int activeTab = -1;

	public BotToolBar(final BotChrome parent) {
		this.parent = parent;
		setFloatable(false);
		setBorder(new EmptyBorder(1, 3, 1, 3));

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
		scriptPlay.addActionListener(this);
		scriptPlay.setToolTipText(BotLocale.PLAYSCRIPT);
		scriptPlay.setFocusable(false);
		scriptPlay.setVisible(true);
		add(scriptPlay);
		scriptStop = new JButton(new ImageIcon(Resources.getImage(Resources.Paths.CONTROL_STOP)));
		scriptStop.addActionListener(this);
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

	@Override
	public void actionPerformed(final ActionEvent e) {
		final Component c = (Component) e.getSource();
		if (c == scriptPlay) {
			new BotScripts(this);
		} else if (c == scriptStop) {
			
		}
	}

	public void addTab() {
		final int n = Bot.bots.size();
		if (n > 0 && !NetworkAccount.getInstance().isVIP()) {
			JOptionPane.showMessageDialog(parent, BotLocale.NEEDVIPMULTITAB, BotLocale.NEEDVIP, JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		final Bot bot = new Bot();
		add(new BotButton("Game", bot), n);
		activateTab(n);
		tabAdd.setVisible(BotChrome.MAX_BOTS - Bot.bots.size() > 1);
		BotChrome.panel.setBot(bot);
		new Thread(bot.threadGroup, bot).start();
	}

	public void closeTab(final int n) {
		final List<Bot> bots = Collections.unmodifiableList(Bot.bots);
		boolean loggedIn = false;
		if (n > 0 && n < bots.size()) {
			final Bot bot = bots.get(n);
			if (bot != null && bot.getClient() != null && bot.multipliers != null && bot.constants != null) {
				final int state = bot.getClient().getLoginIndex() * bot.multipliers.GLOBAL_LOGININDEX;
				loggedIn = state == bot.constants.CLIENTSTATE_11 || state == bot.constants.CLIENTSTATE_12;
			}
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

		final boolean a = getTabCount() > 1;
		if (a) {
			if (getActiveTab() == n) {
				final int x = n == 1 ? 0 : n - 1;
				activateTab(x);
			}
		} else {
			BotChrome.panel.setBot(null);
			activeTab = -1;
		}
		remove(n);
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
				final BotButton b = (BotButton) c;
				b.setActive(a);
				if (a) {
					BotChrome.panel.setBot(b.getBot());
				}
				i++;
			}
		}
		activeTab = n;
	}

	public int getActiveTab() {
		return activeTab;
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

	private final class BotButton extends JButton {
		private static final long serialVersionUID = 1L;
		private final Bot bot;

		public BotButton(final String name, final Bot bot) {
			super(name);
			this.bot = bot;
			setFocusable(false);
			setHorizontalTextPosition(SwingConstants.LEFT);
			setIcon(new ImageIcon(Resources.getImage(Resources.Paths.CROSS_SMALL_GRAY)));
			final Component c = this;
			addMouseMotionListener(new MouseMotionAdapter() {
				@Override
				public void mouseMoved(final MouseEvent e) {
					if (e.getX() > getWidth() - getIcon().getIconWidth()
							&& e.getX() < getWidth() - getIconTextGap()) {
						setIcon(new ImageIcon(Resources.getImage(Resources.Paths.CROSS_SMALL)));
					} else {
						setIcon(new ImageIcon(Resources.getImage(Resources.Paths.CROSS_SMALL_GRAY)));
					}
				}
			});
			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(final MouseEvent e) {
					final int n = getComponentIndex(c);
					if (e.getX() > getWidth() - getIcon().getIconWidth()
							&& e.getX() < getWidth() - getIconTextGap()) {
						closeTab(n);
					} else {
						activateTab(n);
					}
				}

				@Override
				public void mouseExited(final MouseEvent e) {
					setIcon(new ImageIcon(Resources.getImage(Resources.Paths.CROSS_SMALL_GRAY)));
				}
			});
		}

		public void setActive(final boolean active) {
			setFont(getFont().deriveFont(active ? Font.BOLD : 0));
		}

		public Bot getBot() {
			return bot;
		}
	}
}
