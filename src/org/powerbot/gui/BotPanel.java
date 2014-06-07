package org.powerbot.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.powerbot.script.Bot;
import org.powerbot.script.Filter;

class BotPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = -8983015619045562434L;
	private final BotChrome chrome;
	private final Filter<Bot> callback;
	private final JPanel mode;
	private final JLabel logo;
	private final AtomicBoolean logoVisible;
	private final JButton rs3, os;

	public BotPanel(final BotChrome chrome, final Callable<Boolean> pre, final Filter<Bot> callback) {
		this.chrome = chrome;
		this.callback = callback;

		final Dimension d = new Dimension(BotChrome.PANEL_MIN_WIDTH, BotChrome.PANEL_MIN_HEIGHT);
		setSize(d);
		setPreferredSize(d);
		setMinimumSize(d);
		setBackground(chrome.getBackground());

		setLayout(new GridBagLayout());
		final JPanel panel = new JPanel();
		panel.setLayout(getLayout());
		panel.setBackground(getBackground());
		logo = new JLabel();
		panel.add(logo, new GridBagConstraints());
		logoVisible = new AtomicBoolean(true);

		mode = new JPanel();
		mode.setVisible(false);
		mode.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		mode.setBackground(getBackground());
		rs3 = new JButton("RS3");
		mode.add(rs3, new GridBagConstraints());
		os = new JButton("OS");
		mode.add(os, new GridBagConstraints());
		final GridBagConstraints c = new GridBagConstraints();
		c.gridy = 1;
		panel.add(mode, c);

		for (final JButton b : new JButton[]{rs3, os}) {
			b.setBackground(Color.DARK_GRAY);
			b.setForeground(Color.WHITE);
			b.setFont(b.getFont().deriveFont(b.getFont().getSize2D() * 1.5f));
			b.setBorder(BorderFactory.createEmptyBorder(6, 18, 6, 18));
			b.setContentAreaFilled(false);
			b.setOpaque(true);
			b.setBorderPainted(false);
			b.setFocusable(false);
			b.addActionListener(this);
			b.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(final MouseEvent e) {
					final Component c = (Component) e.getSource();
					c.setBackground(Color.GRAY);
					c.setCursor(new Cursor(Cursor.HAND_CURSOR));
				}

				@Override
				public void mouseExited(final MouseEvent e) {
					((Component) e.getSource()).setBackground(Color.DARK_GRAY);
				}
			});
		}

		final JLabel status = new JLabel();
		c.gridy++;
		panel.add(status, c);
		status.setBorder(mode.getBorder());
		final Font f = status.getFont();
		status.setFont(new Font(f.getFamily(), f.getStyle(), f.getSize() + 1));

		add(panel);
		Logger.getLogger("").addHandler(new BotPanelLogHandler(status));

		boolean success = false;
		try {
			success = pre.call();
		} catch (final Exception e) {
			e.printStackTrace();
		}

		if (success) {
			new Thread(new AdPanel(logo, panel)).start();
			mode.setVisible(true);
		}
	}

	public void reset() {
		mode.setVisible(true);
		logo.setVisible(logoVisible.get());
		setVisible(true);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (!(e.getSource() instanceof JButton)) {
			return;
		}
		final JButton b = (JButton) e.getSource();
		mode.setVisible(false);
		logoVisible.set(logo.isVisible());
		logo.setVisible(true);
		final Bot bot = b == os ? new org.powerbot.bot.rt4.Bot(chrome) : new org.powerbot.bot.rt6.Bot(chrome);
		callback.accept(bot);
		Logger.getLogger(BotChrome.class.getName()).info("Starting...");
		new Thread(bot).start();
	}
}
