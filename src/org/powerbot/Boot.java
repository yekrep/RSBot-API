package org.powerbot;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.powerbot.Configuration.OperatingSystem;
import org.powerbot.gui.BotChrome;
import org.powerbot.util.StringUtils;
import org.powerbot.util.TextFormatter;

public class Boot implements Runnable {
	private final static String SWITCH_RESTARTED = "-restarted", SWITCH_DEBUG = "-debug";

	public static void main(final String[] args) {
		if (System.getProperty("os.name").contains("Mac")) {
			System.setProperty("apple.awt.UIElement", "true");
		}

		boolean fork = true;

		for (final String arg : args) {
			if (arg.equalsIgnoreCase(SWITCH_DEBUG) || arg.equalsIgnoreCase(SWITCH_RESTARTED)) {
				fork = false;
			}
		}

		if (fork) {
			fork();
		} else {
			new Boot().run();
		}
	}

	public void run() {
		for (final String arg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
			if (arg.contains("-javaagent:") && !arg.endsWith("jrebel.jar")) {
				return;
			}
		}

		final Logger logger = Logger.getLogger("");
		for (final Handler handler : logger.getHandlers()) {
			logger.removeHandler(handler);
		}
		try {
			LogManager.getLogManager().readConfiguration(new ByteArrayInputStream(StringUtils.getBytesUtf8(
					"java.util.logging.FileHandler.formatter=" + TextFormatter.class.getCanonicalName())));

			final FileHandler h = new FileHandler("%t/" + Configuration.NAME + "-%u.log", 1024 * 32, 1, false);
			try {
				final Field f = FileHandler.class.getDeclaredField("files");
				final boolean a = f.isAccessible();
				f.setAccessible(true);
				final Object o = f.get(h);
				f.setAccessible(a);
				if (o instanceof File[]) {
					System.setProperty("chrome.log", ((File[]) o)[0].getAbsolutePath());
					logger.addHandler(h);
				}
			} catch (final Exception ignored) {
			}
		} catch (final IOException ignored) {
		}
		logger.addHandler(new Handler() {
			@Override
			public void publish(final LogRecord record) {
				if (record == null || record.getMessage() == null) {
					return;
				}
				final String text = record.getMessage().trim();
				if (text.length() == 0) {
					return;
				}
				final int level = record.getLevel().intValue();
				final PrintStream std = level >= Level.WARNING.intValue() ? System.err : System.out;
				std.print('[');
				std.print(record.getLevel().getName());
				std.print("] ");
				final String name = record.getLoggerName().trim();
				if (!name.isEmpty()) {
					std.print(name);
					std.print(": ");
				}
				std.print(text);
				//noinspection ThrowableResultOfMethodCallIgnored
				final Throwable throwable = record.getThrown();
				if (throwable != null) {
					throwable.printStackTrace(std);
				} else {
					std.println();
				}
			}

			@Override
			public void flush() {
			}

			@Override
			public void close() {
			}
		});

		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(final Thread t, final Throwable e) {
				final StringBuilder s = new StringBuilder();
				final String lf = System.getProperty("line.separator", "\n");
				s.append(e.toString()).append(' ');
				for (final StackTraceElement x : e.getStackTrace()) {
					s.append(lf).append('\t').append(x.toString());
				}
				Logger.getLogger("").severe(s.toString());
			}
		});

		if (Configuration.OS == OperatingSystem.MAC) {
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty("apple.awt.UIElement", "false");
		}

		final Sandbox sandbox = new Sandbox();
		sandbox.checkCreateClassLoader();
		sandbox.checkRead(new File(".").getAbsolutePath());
		System.setSecurityManager(sandbox);
		System.setProperty("java.net.preferIPv4Stack", "true");
		System.setProperty("http.keepalive", "false");

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (final Exception ignored) {
				}

				BotChrome.getInstance();
			}
		});
	}

	public static void fork() {
		final List<String> args = new ArrayList<String>();
		args.add("java");

		args.add("-Xmx512m");
		args.add("-Xss2m");
		args.add("-XX:+UseConcMarkSweepGC");

		if (Configuration.OS == OperatingSystem.WINDOWS) {
			args.add("-Dsun.java2d.noddraw=true");
		}

		if (Configuration.OS == OperatingSystem.MAC) {
			final File icon = new File(Configuration.HOME, "icon.png");
			if (icon.isFile()) {
				args.add("-Xdock:icon=" + icon.getAbsolutePath());
			}

			args.add("-Xdock:name=" + Configuration.NAME);
		}

		args.add("-classpath");
		final String location = Boot.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		args.add(StringUtils.urlDecode(location).replaceAll("\\\\", "/"));
		args.add(Boot.class.getCanonicalName());
		args.add(SWITCH_RESTARTED);

		final ProcessBuilder pb = new ProcessBuilder(args);

		if (Configuration.OS == OperatingSystem.MAC) {
			final File java_home = new File("/usr/libexec/java_home");
			if (java_home.canExecute()) {
				try {
					final Process p = Runtime.getRuntime().exec(new String[]{java_home.getPath(), "-v", "1.6"});
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
		}
	}
}
