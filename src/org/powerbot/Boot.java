package org.powerbot;

import org.powerbot.gui.Chrome;
import org.powerbot.log.SystemConsoleHandler;

import javax.swing.*;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class Boot implements Runnable {
	public static void main(String[] params) {
		final Logger logger = Logger.getLogger("");
		for (final Handler handler : logger.getHandlers()) {
			logger.removeHandler(handler);
		}
		logger.addHandler(new SystemConsoleHandler());

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (final Exception ignored) {
		}

		final Chrome chrome = new Chrome();
		chrome.addBot();
	}

	public void run() {
		main(new String[]{});
	}
}
