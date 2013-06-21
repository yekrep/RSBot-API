package org.powerbot;

import org.powerbot.Configuration.OperatingSystem;
import org.powerbot.bot.RSLoader;
import org.powerbot.gui.BotChrome;
import org.powerbot.service.GameAccounts;
import org.powerbot.service.NetworkAccount;
import org.powerbot.util.Sandbox;
import org.powerbot.util.StringUtil;
import org.powerbot.util.Tracker;
import org.powerbot.util.io.CryptFile;
import org.powerbot.util.io.IOHelper;
import org.powerbot.util.io.PrintStreamHandler;
import org.powerbot.util.io.Resources;

import javax.swing.UIManager;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Boot implements Runnable {
	private final static Logger log = Logger.getLogger(Boot.class.getName());
	private final static String SWITCH_RESTARTED = "-restarted", SWITCH_DEBUG = "-debug", SWITCH_VERSION_SHORT = "-v";
	private final static File ICON_TMP = new File(System.getProperty("java.io.tmpdir"), Configuration.NAME.toLowerCase() + ".ico.png");

	public static void main(final String[] args) {
		final Logger logger = Logger.getLogger("");
		for (final Handler handler : logger.getHandlers()) {
			logger.removeHandler(handler);
		}

		boolean restarted = false, debugging = false;

		for (final String arg : args) {
			switch (arg) {
			case SWITCH_DEBUG:
				debugging = true;
				restarted = true;
				break;
			case SWITCH_RESTARTED:
				restarted = true;
				break;
			case SWITCH_VERSION_SHORT:
				System.out.println(Configuration.VERSION);
				return;
			}
		}

		if (Configuration.OS == OperatingSystem.MAC && !ICON_TMP.isFile()) {
			try {
				IOHelper.write(Resources.getResourceURL(Resources.Paths.ICON).openStream(), ICON_TMP);
			} catch (final IOException ignored) {
			}
		}

		logger.addHandler(new PrintStreamHandler());

		if (!restarted) {
			fork(true);
			return;
		}

		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				log.logp(Level.SEVERE, t.getStackTrace()[1].getClassName(), t.getStackTrace()[1].getMethodName(), e.getMessage(), e);
				e.printStackTrace();
			}
		});

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

		StringUtil.newStringUtf8(null); // prevents ClassCircularityError exceptions
		CryptFile.PERMISSIONS.clear();
		final Sandbox sandbox = new Sandbox();
		sandbox.checkRead(Resources.Paths.ROOT);
		System.setSecurityManager(sandbox);
		System.setProperty("java.net.preferIPv4Stack", "true");

		BotChrome.getInstance();
	}

	public void run() {
		main(new String[]{});
	}

	public static void fork(final boolean wait) {
		final List<String> args = new ArrayList<>();
		args.add("java");

		args.add("-Xss6m");
		final long mem = ((com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalPhysicalMemorySize() >> 20;
		args.add("-Xmx" + Math.min(2048, mem / 4) + "m");
		args.add("-XX:MaxPermSize=256m");

		args.add("-XX:+CMSClassUnloadingEnabled");
		args.add("-XX:+UseCodeCacheFlushing");
		args.add("-XX:-UseSplitVerifier");
		args.add("-XX:+UseConcMarkSweepGC");

		if (Configuration.OS == OperatingSystem.MAC) {
			args.add("-Xdock:name=" + Configuration.NAME);
			args.add("-Xdock:icon=" + ICON_TMP.getAbsolutePath());
		}

		args.add("-classpath");
		final String location = Boot.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		args.add(StringUtil.urlDecode(location).replaceAll("\\\\", "/"));
		args.add(Boot.class.getCanonicalName());
		args.add(SWITCH_RESTARTED);

		final ProcessBuilder pb = new ProcessBuilder(args);

		if (wait) {
			pb.inheritIO();
		}

		try {
			final Process p = pb.start();

			if (wait) {
				p.waitFor();
			}
		} catch (final Exception ignored) {
			if (!Configuration.FROMJAR) {
				ignored.printStackTrace();
			}
		}
	}
}
