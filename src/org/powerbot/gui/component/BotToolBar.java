package org.powerbot.gui.component;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.powerbot.core.Bot;
import org.powerbot.core.bot.handlers.ScriptHandler;
import org.powerbot.gui.controller.BotInteract;
import org.powerbot.gui.controller.BotInteract.Action;
import org.powerbot.service.NetworkAccount;
import org.powerbot.util.Tracker;
import org.powerbot.util.io.Resources;

/**
 * @author Paris
 */
public final class BotToolBar extends JToolBar {
	private static final long serialVersionUID = 6279235497882884115L;
	private final JButton add, accounts, signin, play, stop, feedback, input, view;
	private final ImageIcon[] playIcons;

	public BotToolBar() {
		setFloatable(false);
		setBorder(new EmptyBorder(1, 3, 1, 3));
		final int d = 16;

		add = new JButton(new ImageIcon(Resources.getImage(Resources.Paths.ADD)));
		add.setToolTipText(BotLocale.NEWTAB);
		add.setFocusable(false);
		add.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				track(e);
				BotInteract.tabAdd();
			}
		});
		add(add);

		add(Box.createHorizontalStrut(d));

		accounts = new JButton(new ImageIcon(Resources.getImage(Resources.Paths.ADDRESS)));
		accounts.setToolTipText(BotLocale.ACCOUNTS);
		accounts.setFocusable(false);
		accounts.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				track(e);
				BotInteract.showDialog(Action.ACCOUNTS);
			}
		});
		add(accounts);
		signin = new JButton(new ImageIcon(Resources.getImage(Resources.Paths.KEYS)));
		signin.setToolTipText(BotLocale.SIGNIN);
		signin.setFocusable(false);
		signin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				track(e);
				BotInteract.showDialog(Action.SIGNIN);
			}
		});
		add(signin);

		add(Box.createHorizontalGlue());

		playIcons = new ImageIcon[] { new ImageIcon(Resources.getImage(Resources.Paths.PLAY)), new ImageIcon(Resources.getImage(Resources.Paths.PAUSE)) };
		play = new JButton(playIcons[0]);
		play.setToolTipText(BotLocale.PLAYSCRIPT);
		play.setFocusable(false);
		play.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				track(e);
				BotInteract.scriptPlayPause();
			}
		});
		add(play);
		stop = new JButton(new ImageIcon(Resources.getImage(Resources.Paths.STOP)));
		stop.setToolTipText(BotLocale.STOPSCRIPT);
		stop.setFocusable(false);
		stop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				track(e);
				BotInteract.scriptStop();
			}
		});
		add(stop);
		feedback = new JButton(new ImageIcon(Resources.getImage(Resources.Paths.COMMENTS)));
		feedback.setToolTipText(BotLocale.FEEDBACK);
		feedback.setVisible(false);
		feedback.setFocusable(false);
		feedback.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				track(e);
			}
		});
		add(feedback);

		add(Box.createHorizontalStrut(d));

		input = new JButton(new ImageIcon(Resources.getImage(Resources.Paths.KEYBOARD)));
		input.setToolTipText(BotLocale.INPUT);
		input.setFocusable(false);
		input.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				track(e);
				new BotMenuInput().show((Component) e.getSource(), input.getWidth() / 2, input.getHeight() / 2);
			}
		});
		add(input);
		view = new JButton(new ImageIcon(Resources.getImage(Resources.Paths.CONTROLS)));
		view.setToolTipText(BotLocale.VIEW);
		view.setFocusable(false);
		view.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				track(e);
				new BotMenuView().show((Component) e.getSource(), view.getWidth() / 2, input.getHeight() / 2);
			}
		});
		add(view);

		add(Box.createHorizontalStrut(d));

		final JButton about = new JButton(new ImageIcon(Resources.getImage(Resources.Paths.INFO)));
		about.setToolTipText(BotLocale.ABOUT);
		about.setFocusable(false);
		about.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				track(e);
				BotInteract.showDialog(Action.ABOUT);
			}
		});
		add(about);

		updateControls();
	}

	public void track(final ActionEvent e) {
		final Component c = (Component) e.getSource();
		final String s = c == signin ? BotLocale.SIGNIN : ((JButton) c).getToolTipText();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Tracker.getInstance().trackPage("/toolbar", s);
			}
		});
	}

	public void setVisibleEx(final boolean r) {
		accounts.setVisible(r);
		signin.setVisible(r);
	}

	public void updateControls() {
		final NetworkAccount a = NetworkAccount.getInstance();
		add.setEnabled(a.isLoggedIn());
		signin.setToolTipText(a.isLoggedIn() ? a.getDisplayName() : BotLocale.SIGNIN);

		final boolean e = Bot.instantiated();
		for (final Component c : new Component[] { play, stop, input, view }) {
			c.setVisible(e);
		}

		if (e && Bot.instance().getScriptHandler() != null) {
			final ScriptHandler script = Bot.instance().getScriptHandler();
			final boolean active = script != null && script.isActive(), running = active && !script.isPaused();
			play.setIcon(playIcons[running ? 1 : 0]);
			play.setToolTipText(running ? BotLocale.PAUSESCRIPT : active ? BotLocale.RESUMESCRIPT : BotLocale.PLAYSCRIPT);
			stop.setEnabled(active);
		}
	}
}
