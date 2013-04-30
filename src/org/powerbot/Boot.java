package org.powerbot;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.powerbot.bot.RSLoader;
import org.powerbot.gui.BotChrome;
import org.powerbot.gui.component.BotFileChooser;
import org.powerbot.gui.component.BotLocale;
import org.powerbot.ipc.Controller;
import org.powerbot.util.Configuration;
import org.powerbot.util.Configuration.OperatingSystem;
import org.powerbot.util.RestrictedSecurityManager;
import org.powerbot.util.StringUtil;
import org.powerbot.util.io.CryptFile;
import org.powerbot.util.io.IOHelper;
import org.powerbot.util.io.PrintStreamHandler;
import org.powerbot.util.io.Resources;

public class Boot implements Runnable {
	private final static Logger log = Logger.getLogger(Boot.class.getName());
	private final static String SWITCH_RESTARTED = "-restarted", SWITCH_VERSION_SHORT = "-v";
	private final static String ICON_TMP = System.getProperty("java.io.tmpdir") + File.separator + Configuration.NAME.toLowerCase() + ".ico.png";

	public static void main(final String[] args) {
		final Logger logger = Logger.getLogger("");
		for (final Handler handler : logger.getHandlers()) {
			logger.removeHandler(handler);
		}

		boolean restarted = false;

		for (final String arg : args) {
			switch (arg) {
			case SWITCH_RESTARTED:
				restarted = true;
				break;
			case SWITCH_VERSION_SHORT:
				System.out.println(Configuration.VERSION);
				return;
			}
		}

		if (!restarted && Configuration.OS == OperatingSystem.MAC) {
			try {
				IOHelper.write(Resources.getResourceURL(Resources.Paths.ICON).openStream(), new File(ICON_TMP));
			} catch (final IOException ignored) {
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

		final int xmx = (int) (Runtime.getRuntime().maxMemory() >> 20), xmx0 = 1024;

		if (xmx < xmx0 && !restarted) {
			log.severe(String.format("Default heap size of %sm too small, restarting with %sm", xmx, xmx0));
			fork("-Xmx" + xmx0 + "m ", SWITCH_RESTARTED);
			return;
		}

		try {
			if (Configuration.OS == OperatingSystem.MAC) {
				System.setProperty("apple.laf.useScreenMenuBar", "true");
			}
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (final Exception ignored) {
		}

		try {
			URL.setURLStreamHandlerFactory(new URLStreamHandlerFactory() {
				@Override
				public URLStreamHandler createURLStreamHandler(final String protocol) {
					switch (protocol) {
					case "http":
						return new sun.net.www.protocol.http.Handler();
					case "https":
						return new sun.net.www.protocol.https.Handler();
					case "file":
						return new sun.net.www.protocol.file.Handler();
					case "jar":
						return new sun.net.www.protocol.jar.Handler();
					case "ftp":
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

		verifyAssert();

		StringUtil.newStringUtf8(null); // prevents ClassCircularityError exceptions
		CryptFile.PERMISSIONS.clear();
		System.setSecurityManager(new RestrictedSecurityManager(RSLoader.class, BotFileChooser.class));
		System.setProperty("java.net.preferIPv4Stack", "true");
		if (!Configuration.URLs.TESTING) {
			System.setProperty("sun.net.spi.nameservice.nameservers", RestrictedSecurityManager.DNS1 + "," + RestrictedSecurityManager.DNS2);
			System.setProperty("sun.net.spi.nameservice.provider.1", "dns,sun");
		}

		BotChrome.getInstance();
	}

	public void run() {
		main(new String[]{});
	}

	public static void fork() {
		fork("", "");
	}

	private static void fork(String options, String args) {
		if (!options.contains("-Xss")) {
			options += " -Xss6m";
		}
		if (!options.contains("-Xmx")) {
			options += " -Xmx" + ((int) (Runtime.getRuntime().maxMemory() >> 20)) + "m";
		}
		if (!options.contains("-XX:MaxPermSize=")) {
			options += " -XX:MaxPermSize=" + Math.max(256, Runtime.getRuntime().maxMemory() >> 22) + "m";
		}

		final List<String> flags = new ArrayList<>(4);
		flags.add("-XX:+CMSClassUnloadingEnabled");
		flags.add("-XX:+UseCodeCacheFlushing");
		flags.add("-XX:-UseSplitVerifier");
		if (Runtime.getRuntime().availableProcessors() > 1) {
			flags.add("-XX:+UseConcMarkSweepGC");
		}
		if (Configuration.OS == OperatingSystem.MAC) {
			flags.add("-Xdock:name=" + Configuration.NAME);
			flags.add("-Xdock:icon=" + ICON_TMP);
		}
		for (final String flag : flags) {
			if (!options.contains(flag)) {
				options += " " + flag;
			}
		}

		if (!args.contains(SWITCH_RESTARTED)) {
			args += " " + SWITCH_RESTARTED;
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
		if (Configuration.OS == OperatingSystem.WINDOWS) {
			JOptionPane.showMessageDialog(null, txt, BotLocale.ERROR, JOptionPane.ERROR_MESSAGE);
		}
	}

	public static boolean verify() {
		final RuntimeMXBean mx = ManagementFactory.getRuntimeMXBean();

		for (String arg : mx.getInputArguments()) {
			if (arg.contains("javaagent") || arg.contains("bootclass")) {
				return false;
			}
		}

		return true;
	}

	public static void verifyAssert() {
		if (!verify()) {
			System.exit(1);
		}
	}
}
