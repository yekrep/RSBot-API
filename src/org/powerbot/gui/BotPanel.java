package org.powerbot.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.powerbot.misc.Resources;
import org.powerbot.script.Bot;
import org.powerbot.script.Filter;

class BotPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = -8983015619045562434L;
	private final BotChrome chrome;
	private final Filter<Bot> callback;
	private final JPanel mode;
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
		final JLabel logo = new JLabel(new ImageIcon(Resources.getImage(Resources.Paths.ARROWS)));
		panel.add(logo, new GridBagConstraints());

		mode = new JPanel();
		mode.setVisible(false);
		mode.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		mode.setBackground(getBackground());
		rs3 = new JButton("RS3");
		rs3.setBackground(getBackground());
		rs3.setFocusable(false);
		rs3.addActionListener(this);
		mode.add(rs3, new GridBagConstraints());
		os = new JButton("OS");
		os.setBackground(getBackground());
		os.setFocusable(false);
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

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (!(e.getSource() instanceof JButton)) {
			return;
		}
		final JButton b = (JButton) e.getSource();
		mode.setVisible(false);
		final Bot bot = b == os ? new org.powerbot.bot.rt4.Bot(chrome) : new org.powerbot.bot.rt6.Bot(chrome);
		callback.accept(bot);
		Logger.getLogger(BotChrome.class.getName()).info("Starting...");
		new Thread(bot.threadGroup, bot).start();
	}
}
