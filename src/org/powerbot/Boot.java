package org.powerbot;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.powerbot.gui.BotChrome;
import org.powerbot.util.Configuration;
import org.powerbot.util.Configuration.OperatingSystem;
import org.powerbot.util.RestrictedSecurityManager;
import org.powerbot.util.StringUtil;
import org.powerbot.util.io.IniParser;
import org.powerbot.util.io.SystemConsoleHandler;

public class Boot implements Runnable {
	private final static Logger log = Logger.getLogger(Boot.class.getName());

	public static void main(final String[] args) {
		final Logger logger = Logger.getLogger("");
		for (final Handler handler : logger.getHandlers()) {
			logger.removeHandler(handler);
		}
		logger.addHandler(new SystemConsoleHandler());

		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				log.logp(Level.SEVERE, t.getStackTrace()[1].getClassName(), t.getStackTrace()[1].getMethodName(), e.getMessage(), e);
			}
		});

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

		int req = -1;

		final File settingsFile = new File(Configuration.BOOTSETTINGS);
		if (settingsFile.exists()) {
			Map<String, Map<String, String>> settings = null;
			try {
				settings = IniParser.deserialise(settingsFile);
			} catch (final IOException ignored) {
			}
			if (settings != null && settings.containsKey(IniParser.EMPTYSECTION)) {
				final Map<String, String> conf = settings.get(IniParser.EMPTYSECTION);
				if (conf.containsKey("memory")) {
					try {
						req = Math.max(256, Integer.parseInt(conf.get("memory")));
					} catch (final NumberFormatException ignored) {
						req = -1;
					}
				}
				if (conf.containsKey("developer")) {
					Configuration.DEVMODE = IniParser.parseBool(conf.get("developer"));
				}
				if (conf.containsKey("scripts")) {
					Configuration.SCRIPTPATH = conf.get("scripts");
				}
			}
		}

		if (req == -1 && !Configuration.DEVMODE) {
			req = 768;
		}

		long mem = Runtime.getRuntime().maxMemory() / 1024 / 1024;

		if (mem < req && !restarted) {
			log.severe(String.format("Default heap size of %sm too small, restarting with %sm", mem, req));
			String cmd = Configuration.OS == OperatingSystem.WINDOWS ? "javaw" : "java";
			String location = Boot.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			location = StringUtil.urlDecode(location).replaceAll("\\\\", "/");
			cmd += " -Xss6m -Xmx" + req + "m -classpath \"" + location + "\" \"" + Boot.class.getCanonicalName() + "\" " + SWITCH_RESTARTED;
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

		if (!getLock()) {
			final String msg = "An instance of " + Configuration.NAME + " is already running";
			log.severe(msg);
			if (!Configuration.DEVMODE) {
				JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
			}
			return;
		}

		System.setSecurityManager(new RestrictedSecurityManager());
		System.setProperty("java.net.preferIPv4Stack", "true");
		System.setProperty("sun.net.spi.nameservice.nameservers", RestrictedSecurityManager.DNS1 + "," + RestrictedSecurityManager.DNS2);
		System.setProperty("sun.net.spi.nameservice.provider.1", "dns,sun");

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (final Exception ignored) {
		}

		BotChrome.getInstance();
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
