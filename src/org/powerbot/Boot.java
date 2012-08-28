package org.powerbot;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.UIManager;

import org.powerbot.gui.BotChrome;
import org.powerbot.gui.component.BotLocale;
import org.powerbot.ipc.Controller;
import org.powerbot.ipc.ScheduledChecks;
import org.powerbot.util.Configuration;
import org.powerbot.util.Configuration.OperatingSystem;
import org.powerbot.util.RestrictedSecurityManager;
import org.powerbot.util.StringUtil;
import org.powerbot.util.io.CryptFile;
import org.powerbot.util.io.PrintStreamHandler;

public class Boot implements Runnable {
	private final static Logger log = Logger.getLogger(Boot.class.getName());
	private final static String SWITCH_DEV = "-dev", SWITCH_RESTARTED = "-restarted", SWITCH_VERSION_SHORT = "-v";
	public final static String SWITCH_NEWTAB = "-newtab";

	public static void main(final String[] args) {
		final Logger logger = Logger.getLogger("");
		for (final Handler handler : logger.getHandlers()) {
			logger.removeHandler(handler);
		}

		boolean restarted = false, newtab = false;

		for (final String arg : args) {
			switch (arg) {
			case SWITCH_DEV:
				Configuration.DEVMODE = true;
				break;
			case SWITCH_RESTARTED:
				restarted = true;
				break;
			case SWITCH_NEWTAB:
				newtab = true;
				break;
			case SWITCH_VERSION_SHORT:
				System.out.println(Configuration.VERSION);
				return;
			}
		}

		logger.addHandler(new PrintStreamHandler());

		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				log.logp(Level.SEVERE, t.getStackTrace()[1].getClassName(), t.getStackTrace()[1].getMethodName(), e.getMessage(), e);
				e.printStackTrace();
			}
		});

		final int req = 768;
		long mem = Runtime.getRuntime().maxMemory() / 1024 / 1024;

		if (mem < 768 && !restarted && !Configuration.DEVMODE) {
			log.severe(String.format("Default heap size of %sm too small, restarting with %sm", mem, req));
			fork("-Xmx" + req + "m ", SWITCH_RESTARTED);
			return;
		}

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (final Exception ignored) {
		}

		try {
			URL.setURLStreamHandlerFactory(new URLStreamHandlerFactory() {
				@Override
				public URLStreamHandler createURLStreamHandler(final String protocol) {
					if (protocol.equals("http")) {
						return new sun.net.www.protocol.http.Handler();
					} else if (protocol.equals("https")) {
						return new sun.net.www.protocol.https.Handler();
					} else if (protocol.equals("file")) {
						return new sun.net.www.protocol.file.Handler();
					} else if (protocol.equals("jar")) {
						return new sun.net.www.protocol.jar.Handler();
					} else if (protocol.equals("ftp")) {
						return new sun.net.www.protocol.ftp.Handler();
					}
					return null;
				}
			});
		} catch (final Throwable ignored) {
			log.severe("Could not set URL stream handler factory");
			System.exit(1);
			return;
		}

		final String appdata = System.getenv("APPDATA"), home = System.getProperty("user.home");
		final String root = appdata != null && new File(appdata).isDirectory() ? appdata : home == null ? "~" : home;
		final File store = new File(root + File.separator + Configuration.NAME + ".db");
		if (store.isFile()) {
			store.delete();
		}

		if (!Controller.getInstance().isBound()) {
			log.severe("Could not bind to local port");
			System.exit(1);
			return;
		}

		if (Controller.getInstance().isAnotherInstanceLoading()) {
			message(String.format("Another instance of %s is loading", Configuration.NAME));
			System.exit(1);
			return;
		}

		if (Controller.getInstance().getRunningInstances() >= Controller.MAX_INSTANCES) {
			message("Maximum number of bots are running");
			System.exit(1);
			return;
		}

		StringUtil.newStringUtf8(null); // prevents ClassCircularityError exceptions
		CryptFile.PERMISSIONS.clear();
		System.setSecurityManager(new RestrictedSecurityManager());
		System.setProperty("java.net.preferIPv4Stack", "true");
		if (!Configuration.URLs.TESTING) {
			System.setProperty("sun.net.spi.nameservice.nameservers", RestrictedSecurityManager.DNS1 + "," + RestrictedSecurityManager.DNS2);
			System.setProperty("sun.net.spi.nameservice.provider.1", "dns,sun");
		}

		final Timer timer = new Timer(1000 * 60 * 10, new ScheduledChecks());
		timer.setCoalesce(false);
		timer.setInitialDelay(1000 * 60 * 1);
		timer.start();

		final BotChrome chrome = BotChrome.getInstance();
		if (newtab) {
			chrome.toolbar.addTab();
		}
		chrome.toolbar.tabAdd.setEnabled(true);
	}

	public void run() {
		main(new String[]{});
	}

	public static void fork(final String args) {
		fork("", args);
	}

	private static void fork(String options, String args) {
		if (!options.contains("-Xss")) {
			options += " -Xss6m";
		}
		if (!options.contains("-Xmx")) {
			options += " -Xmx" + (Runtime.getRuntime().maxMemory() / 1024 / 1024) + "m";
		}
		if (!options.contains("-XX:MaxPermSize=")) {
			options += " -XX:MaxPermSize=" + Math.max(256, Runtime.getRuntime().maxMemory() / 1024 / 1024 / 4) + "m";
		}
		for (final String flag : new String[] {"-XX:+UseConcMarkSweepGC", "-XX:+CMSClassUnloadingEnabled", "-XX:+UseCodeCacheFlushing"}) {
			if (!options.contains(flag)) {
				options += " " + flag;
			}
		}
		if (!args.contains(SWITCH_RESTARTED)) {
			args += " " + SWITCH_RESTARTED;
		}
		if (Configuration.DEVMODE && !args.contains(SWITCH_DEV)) {
			args += " " + SWITCH_DEV;
		}
		String location = Boot.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		location = StringUtil.urlDecode(location).replaceAll("\\\\", "/");
		final String cmd = "java " + options.trim() + " -classpath \"" + location + "\" \"" + Boot.class.getCanonicalName() + "\" " + args.trim();
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
	}

	public static void message(final String txt) {
		log.severe(txt);
		if (!Configuration.DEVMODE && Configuration.OS == OperatingSystem.WINDOWS) {
			JOptionPane.showMessageDialog(null, txt, BotLocale.ERROR, JOptionPane.ERROR_MESSAGE);
		}
	}
}
