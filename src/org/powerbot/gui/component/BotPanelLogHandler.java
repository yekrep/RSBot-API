package org.powerbot.gui.component;

import java.awt.Color;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.powerbot.bot.Bot;
import org.powerbot.gui.BotChrome;
import org.powerbot.loader.ClientLoader;
import org.powerbot.util.Configuration;
import org.powerbot.util.LoadUpdates;

/**
 * @author Paris
 */
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
		if (!(src.equals(BotChrome.class.getName()) || src.equals(LoadUpdates.class.getName()) ||
				src.equals(Bot.class.getName()) || src.equals(BotComposite.class.getName()) ||
				src.equals(ClientLoader.class.getName()))) {
			return;
		}

		Color c = new Color(200, 200, 200);
		final String title = record.getParameters() != null && record.getParameters().length == 1 ? (String) record.getParameters()[0] : null;
		if (record.getLevel() == Level.SEVERE || record.getLevel() == Level.WARNING) {
			c = new Color(255, 87, 71);
		} else {
			record.setMessage("");
		}
		label.setForeground(c);
		label.setText(record.getMessage());

		if (title != null && title.equals("Outdated")) {
			final String msg = Configuration.NAME + " needs to be repaired after a recent game update.\nThis usually takes 1-5 days so please wait patiently.\n\n" +
					"You do not need to do anything, an update will be automatically downloaded for you.\n" +
					"All your scripts will work normally afterwards.\n\n" +
					"Please do not post or send in support messages about this as we are already aware.\n" +
					"Check our website forums and twitter for the latest info.";
			JOptionPane.showMessageDialog(BotChrome.getInstance(), msg, title, JOptionPane.INFORMATION_MESSAGE);
		}
	}
}
