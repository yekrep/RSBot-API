package org.powerbot.os.gui;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class BotPanel extends JPanel {

	public BotPanel() {
		setBackground(Color.BLACK);
		setForeground(Color.WHITE);

		setLayout(new GridBagLayout());

		final JLabel status = new JLabel();
		add(status);

		final Color[] c = {new Color(200, 200, 200), new Color(255, 87, 71)};
		Logger.getLogger(BotPanel.class.getName()).addHandler(new Handler() {
			@Override
			public void publish(final LogRecord r) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						status.setForeground(c[r.getLevel().intValue() >= Level.WARNING.intValue() ? 1 : 0]);
						status.setText(r.getMessage());
					}
				});
			}

			@Override
			public void flush() {
			}

			@Override
			public void close() {
			}
		});
	}
}
