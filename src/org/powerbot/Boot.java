package org.powerbot;

import java.io.File;
import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Logger;

import javax.swing.UIManager;

import org.powerbot.gui.BotChrome;
import org.powerbot.log.SystemConsoleHandler;
import org.powerbot.util.Configuration;
import org.powerbot.util.RestrictedSecurityManager;
import org.powerbot.util.StringUtil;
import org.powerbot.util.Configuration.OperatingSystem;

public class Boot implements Runnable {
	public static void main(final String[] args) {
		final Logger logger = Logger.getLogger("");
		for (final Handler handler : logger.getHandlers()) {
			logger.removeHandler(handler);
		}
		logger.addHandler(new SystemConsoleHandler());

		boolean restarted = false;

		final String SWITCH_DEV = "-dev";
		final String SWITCH_RESTARTED = "-restarted";

		for (final String arg : args) {
			if (arg.equals(SWITCH_DEV)) {
				Configuration.DEVMODE = true;
			} else if (arg.equals(SWITCH_RESTARTED)) {
				restarted = true;
			}
		}

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (final Exception ignored) {
		}

		final int req = 768;
		long mem = Runtime.getRuntime().maxMemory() / 1024 / 1024;

		if (mem < req && !Configuration.DEVMODE && !restarted) {
			Logger.getLogger(Boot.class.getName()).severe(String.format("Default heap size of %sm too small, restarting with %sm", mem, req));
			String cmd = Configuration.OS == OperatingSystem.WINDOWS ? "javaw" : "java";
			String location = Boot.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			location = StringUtil.urlDecode(location).replaceAll("\\\\", "/");
			cmd += " -Xmx" + req + "m -classpath \"" + location + "\" \"" + Boot.class.getCanonicalName() + "\" " + SWITCH_RESTARTED;
			final Runtime run = Runtime.getRuntime();
			try {
				if (Configuration.OS == OperatingSystem.MAC || Configuration.OS == OperatingSystem.LINUX) {
					run.exec(new String[]{"/bin/sh", "-c", cmd});
				} else {
					run.exec(cmd);
				}
				return;
			} catch (final IOException ignored) {
			}
			return;
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
