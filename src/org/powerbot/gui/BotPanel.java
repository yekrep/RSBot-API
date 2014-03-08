package org.powerbot.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.powerbot.misc.Resources;
import org.powerbot.script.Bot;

class BotPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = -8983015619045562434L;
	private final BotChrome chrome;
	private final JPanel mode;
	private final JButton rs3, os;

	public BotPanel(final BotChrome chrome) {
		this.chrome = chrome;
		final Dimension d = new Dimension(BotChrome.PANEL_MIN_WIDTH, BotChrome.PANEL_MIN_HEIGHT);
		setSize(d);
		setPreferredSize(d);
		setMinimumSize(d);
		setBackground(chrome.getBackground());

		setLayout(new GridBagLayout());
		final JPanel panel = new JPanel();
		panel.setLayout(getLayout());
		panel.setBackground(getBackground());
		final JLabel logo = new JLabel(new ImageIcon(Resources.getImage(Resources.Paths.ARROWS)));
		panel.add(logo, new GridBagConstraints());

		mode = new JPanel();
		mode.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		mode.setBackground(getBackground());
		rs3 = new JButton("RS3");
		rs3.addActionListener(this);
		mode.add(rs3, new GridBagConstraints());
		os = new JButton("OS");
		os.addActionListener(this);
		mode.add(os, new GridBagConstraints());
		final GridBagConstraints c = new GridBagConstraints();
		c.gridy = 1;
		panel.add(mode, c);

		final JLabel status = new JLabel();
		c.gridy++;
		panel.add(status, c);
		status.setBorder(mode.getBorder());
		final Font f = status.getFont();
		status.setFont(new Font(f.getFamily(), f.getStyle(), f.getSize() + 1));

		add(panel);
		Logger.getLogger("").addHandler(new BotPanelLogHandler(status));

		new Thread(new AdPanel(logo, panel)).start();
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (!(e.getSource() instanceof JButton)) {
			return;
		}
		final JButton b = (JButton) e.getSource();
		mode.setVisible(false);
		final Bot bot = b == os ? new org.powerbot.bot.os.Bot(chrome) : new org.powerbot.bot.rs3.Bot(chrome);
		if (b == os) {
			chrome.overlay.dispose();
		}
		chrome.bot.set(bot);
		Logger.getLogger(BotChrome.class.getName()).info("Starting...");
		new Thread(bot.threadGroup, bot).start();
	}
}
