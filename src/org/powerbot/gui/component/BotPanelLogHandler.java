package org.powerbot.gui.component;

import java.awt.Color;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.swing.JLabel;

import org.powerbot.bot.Bot;
import org.powerbot.gui.BotChrome;
import org.powerbot.misc.UpdateCheck;

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
		if (!(src.equals(BotChrome.class.getName()) || src.equals(UpdateCheck.class.getName()) ||
				src.equals(Bot.class.getName()))) {
			return;
		}

		Color c = new Color(200, 200, 200);
		if (record.getLevel().intValue() >= Level.WARNING.intValue()) {
			c = new Color(255, 87, 71);
		}
		label.setForeground(c);
		label.setText(record.getLevel().intValue() <= Level.WARNING.intValue() ? "" : record.getMessage());
	}
}
