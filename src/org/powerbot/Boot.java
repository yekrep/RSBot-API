package org.powerbot;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIManager;

import org.powerbot.Configuration.OperatingSystem;
import org.powerbot.gui.BotChrome;
import org.powerbot.util.Sandbox;
import org.powerbot.util.StringUtil;
import org.powerbot.util.io.CryptFile;
import org.powerbot.util.io.IOHelper;
import org.powerbot.util.io.PrintStreamHandler;
import org.powerbot.util.io.Resources;

public class Boot implements Runnable {
	private final static Logger log = Logger.getLogger(Boot.class.getName());
	private final static String SWITCH_RESTARTED = "-restarted", SWITCH_DEBUG = "-debug", SWITCH_VERSION_SHORT = "-v";
	private final static File ICON_TMP = new File(System.getProperty("java.io.tmpdir"), Configuration.NAME.toLowerCase() + ".ico.png");

	public static void main(final String[] args) {
		final Logger logger = Logger.getLogger("");
		for (final Handler handler : logger.getHandlers()) {
			logger.removeHandler(handler);
		}
		logger.addHandler(new PrintStreamHandler());

		boolean restarted = false;

		for (final String arg : args) {
			if (arg.equalsIgnoreCase(SWITCH_DEBUG) || arg.equalsIgnoreCase(SWITCH_RESTARTED)) {
				restarted = true;
			} else if (arg.equalsIgnoreCase(SWITCH_VERSION_SHORT)) {
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

		if (!restarted) {
			fork();
			return;
		}

		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(final Thread t, final Throwable e) {
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

		StringUtil.newStringUtf8(null); // prevents ClassCircularityError exceptions
		CryptFile.PERMISSIONS.clear();
		final Sandbox sandbox = new Sandbox();
		sandbox.checkRead(Resources.Paths.ROOT);
		sandbox.checkCreateClassLoader();
		System.setSecurityManager(sandbox);
		System.setProperty("java.net.preferIPv4Stack", "true");
		System.setProperty("http.keepalive", "false");

		BotChrome.getInstance();
	}

	public void run() {
		main(new String[]{});
	}

	public static void fork() {
		final List<String> args = new ArrayList<String>();
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

		if (Configuration.JAVA6 && Configuration.OS == OperatingSystem.MAC) {
			final File java_home = new File("/usr/libexec/java_home");
			if (java_home.canExecute()) {
				try {
					final Process p = Runtime.getRuntime().exec(new String[] {java_home.getPath(), "-v", "1.6"});
					final BufferedReader stdin = new BufferedReader(new InputStreamReader(p.getInputStream()));
					final String home = stdin.readLine();
					if (home != null && !home.isEmpty() && new File(home).isDirectory()) {
						pb.environment().put("JAVA_HOME", home);
					}
					stdin.close();
				} catch (final IOException ignored) {
				}
			}
		}

		try {
			pb.start();
		} catch (final Exception ignored) {
			if (!Configuration.FROMJAR) {
				ignored.printStackTrace();
			}
		}
	}
}
