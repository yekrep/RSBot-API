package org.powerbot.gui.component;

import org.powerbot.util.io.Resources;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * @author Paris
 */
public class DisplayPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JLabel status = new JLabel(), info = new JLabel();
	protected DisplayPanelLogHandler handler = new DisplayPanelLogHandler();

	public DisplayPanel(Dimension dimension) {
		setSize(dimension);
		setPreferredSize(dimension);
		setMinimumSize(dimension);

		setLayout(new GridBagLayout());
		add(new JLabel(new ImageIcon(Resources.getImage(Resources.Paths.ARROWS))));
		status.setFont(status.getFont().deriveFont(Font.BOLD, 24));
		status.setForeground(Color.WHITE);
		add(status);
		info.setFont(info.getFont().deriveFont(0, 14));
		final GridBagConstraints c = new GridBagConstraints();
		c.gridy = 1;
		c.ipady = 25;
		c.gridwidth = 2;
		add(info, c);
	}

	public DisplayPanel() {
		this(new Dimension(0, 0));
	}

	public class DisplayPanelLogHandler extends Handler {
		@Override
		public void close() throws SecurityException {
		}

		@Override
		public void flush() {
		}

		@Override
		public void publish(final LogRecord record) {
			Color c = new Color(149, 156, 171);
			if (record.getLevel() == Level.SEVERE || record.getLevel() == Level.WARNING) {
				status.setText("  Unavailable");
				c = new Color(255, 87, 71);
			} else {
				status.setText("  Loading...");
			}
			info.setForeground(c);
			info.setText(record.getMessage());
		}
	}
}
