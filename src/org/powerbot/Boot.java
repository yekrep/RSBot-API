package org.powerbot;

import org.powerbot.gui.Chrome;
import org.powerbot.log.SystemConsoleHandler;

import java.util.logging.Handler;
import java.util.logging.Logger;

public class Boot implements Runnable {
	public static void main(String[] params) {
		final Logger logger = Logger.getLogger("");
		for (final Handler handler : logger.getHandlers()) {
			logger.removeHandler(handler);
		}
		logger.addHandler(new SystemConsoleHandler());

		final Chrome chrome = new Chrome();
		chrome.addBot();
	}

	@Override
	public void run() {
		main(new String[] { });
	}
}
