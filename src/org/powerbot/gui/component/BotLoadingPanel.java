package org.powerbot.gui.component;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.powerbot.game.GameDefinition;
import org.powerbot.game.bot.Bot;
import org.powerbot.util.io.Resources;

public final class BotLoadingPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	public final JLabel status = new JLabel(), info = new JLabel();
	private static final Map<ThreadGroup, LogRecord> logRecord = new HashMap<ThreadGroup, LogRecord>();
	private ThreadGroup listeningGroup = null;
	private final BotLoadingPanelLogHandler handler;

	public BotLoadingPanel() {
		setBackground(Color.BLACK);
		setLayout(new GridBagLayout());
		add(new JLabel(new ImageIcon(Resources.getImage(Resources.Paths.ARROWS))));
		status.setFont(status.getFont().deriveFont(Font.BOLD, 24));
		status.setForeground(Color.WHITE);
		status.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
		add(status);
		info.setFont(info.getFont().deriveFont(0, 14));
		final GridBagConstraints c = new GridBagConstraints();
		c.gridy = 1;
		c.ipady = 25;
		c.gridwidth = 2;
		add(info, c);
		handler = new BotLoadingPanelLogHandler(this);
		Logger.getLogger(GameDefinition.class.getName()).addHandler(handler);
		Logger.getLogger(Bot.class.getName()).addHandler(handler);
	}

	public void set(final ThreadGroup threadGroup) {
		this.listeningGroup = threadGroup;
		final LogRecord record;
		if (threadGroup != null && (record = logRecord.get(threadGroup)) != null) {
			handler.publish(record);
		}
	}

	private final class BotLoadingPanelLogHandler extends Handler {
		final BotLoadingPanel panel;

		public BotLoadingPanelLogHandler(final BotLoadingPanel panel) {
			this.panel = panel;
		}

		@Override
		public void close() throws SecurityException {
		}

		@Override
		public void flush() {
		}

		@Override
		public void publish(final LogRecord record) {
			logRecord.put(Thread.currentThread().getThreadGroup(), record);
			if (listeningGroup != null && Thread.currentThread().getThreadGroup() != listeningGroup) {
				return;
			}
			Color c = new Color(149, 156, 171);
			final String title = record.getParameters() != null && record.getParameters().length == 1 ? (String) record.getParameters()[0] : null;
			if (record.getLevel() == Level.SEVERE || record.getLevel() == Level.WARNING) {
				if (title == null) {
					panel.status.setText("Unavailable");
				}
				c = new Color(255, 87, 71);
			} else if (record.getLevel() == Level.INFO) {
				if (title == null) {
					panel.status.setText("Loading...");
				}
			}
			if (title != null) {
				panel.status.setText(title);
			}
			panel.info.setForeground(c);
			panel.info.setText(record.getMessage());
		}
	}
}
