package org.powerbot;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.util.logging.Handler;
import java.util.logging.Logger;

import javax.swing.UIManager;

import org.powerbot.gui.BotChrome;
import org.powerbot.util.Configuration;
import org.powerbot.util.Configuration.OperatingSystem;
import org.powerbot.util.RestrictedSecurityManager;
import org.powerbot.util.StringUtil;
import org.powerbot.util.io.SystemConsoleHandler;

public class Boot implements Runnable {
	private final static Logger log = Logger.getLogger(Boot.class.getName());

	public static void main(final String[] args) {
		final Logger logger = Logger.getLogger("");
		for (final Handler handler : logger.getHandlers()) {
			logger.removeHandler(handler);
		}
		logger.addHandler(new SystemConsoleHandler());

		if (!getLock()) {
			log.severe("An instance of " + Configuration.NAME + " is already running");
			return;
		}

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
			log.severe(String.format("Default heap size of %sm too small, restarting with %sm", mem, req));
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

	private static boolean getLock() {
		final File tmpfile = new File(System.getProperty("java.io.tmpdir"), Configuration.NAME + ".lck");
		try {
			final RandomAccessFile tmpraf = new RandomAccessFile(tmpfile, "rw");
			final FileLock tmplock = tmpraf.getChannel().tryLock();
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					try {
						if (tmplock != null) {
							tmplock.release();
						}
						if (tmpraf != null) {
							tmpraf.close();
						}
					} catch (final IOException ignored) {
					}
					tmpfile.delete();
				}
			});
			return tmplock != null;
		} catch (final IOException ignored) {
		}
		return false;
	}
}
