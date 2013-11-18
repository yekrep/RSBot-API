package org.powerbot.gui.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.powerbot.gui.BotChrome;
import org.powerbot.util.io.Resources;

public class BotPanel extends JPanel {
	private static final long serialVersionUID = -8983015619045562434L;

	public BotPanel(final Component parent) {
		final Dimension d = new Dimension(BotChrome.PANEL_MIN_WIDTH, BotChrome.PANEL_MIN_HEIGHT);
		setSize(d);
		setPreferredSize(d);
		setMinimumSize(d);
		setFocusable(true);
		setFocusTraversalKeysEnabled(false);
		setBackground(Color.black);

		setLayout(new GridBagLayout());
		final JPanel panel = new JPanel();
		panel.setLayout(getLayout());
		panel.setBackground(getBackground());
		panel.add(new JLabel(new ImageIcon(Resources.getImage(Resources.Paths.ARROWS))), new GridBagConstraints());
		final GridBagConstraints c = new GridBagConstraints();
		c.gridy = 1;
		final JLabel status;
		panel.add(status = new JLabel(), c);
		status.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		final Font f = status.getFont();
		status.setFont(new Font(f.getFamily(), f.getStyle(), f.getSize() + 1));
		add(panel);
		Logger.getLogger("").addHandler(new BotPanelLogHandler(status));
	}
}