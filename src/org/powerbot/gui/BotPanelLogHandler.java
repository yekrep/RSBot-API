package org.powerbot.gui;

import java.awt.Color;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

final class BotPanelLogHandler extends Handler {
	private final JLabel label;

	public BotPanelLogHandler(final JLabel label) {
		this.label = label;
	}

	@Override
	public void close() throws SecurityException {
	}

	@Override
	public void flush() {
	}

	@Override
	public void publish(final LogRecord record) {
		String src = record.getLoggerName();
		if (src == null || src.isEmpty()) {
			return;
		}
		final int x = src.indexOf('$');
		if (x > 0) {
			src = src.substring(0, x);
		}
		if (!(src.equals(BotLauncher.class.getName()) || src.equals(org.powerbot.bot.rt6.Bot.class.getName()) || src.equals(org.powerbot.bot.rt4.Bot.class.getName()))) {
			return;
		}

		final Color c = record.getLevel().intValue() >= Level.WARNING.intValue() ? new Color(255, 87, 71) : new Color(200, 200, 200);
		final String txt = "<html><div style=\"text-align: center;\">" + record.getMessage().replace("\n", "<br>") + "</div></html>";

		if (SwingUtilities.isEventDispatchThread()) {
			label.setForeground(c);
			label.setText(txt);
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					label.setForeground(c);
					label.setText(txt);
				}
			});
		}
	}
}
