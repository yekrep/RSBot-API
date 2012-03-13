package org.powerbot;

import java.io.File;
import java.util.logging.Handler;
import java.util.logging.Logger;

import javax.swing.UIManager;

import org.powerbot.gui.BotChrome;
import org.powerbot.log.SystemConsoleHandler;
import org.powerbot.util.Configuration;
import org.powerbot.util.RestrictedSecurityManager;

public class Boot implements Runnable {
	public static void main(final String[] params) {
		final Logger logger = Logger.getLogger("");
		for (final Handler handler : logger.getHandlers()) {
			logger.removeHandler(handler);
		}
		logger.addHandler(new SystemConsoleHandler());

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (final Exception ignored) {
		}

		if (!new File(Configuration.STORE).isHidden()) {
		}
		System.setSecurityManager(new RestrictedSecurityManager());
		System.setProperty("java.net.preferIPv4Stack", "true");
		System.setProperty("sun.net.spi.nameservice.nameservers", RestrictedSecurityManager.DNS1 + "," + RestrictedSecurityManager.DNS2);
		System.setProperty("sun.net.spi.nameservice.provider.1", "dns,sun");

		new BotChrome();
	}

	public void run() {
		main(new String[]{});
	}
}
