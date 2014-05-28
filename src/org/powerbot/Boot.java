package org.powerbot;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.powerbot.Configuration.OperatingSystem;
import org.powerbot.gui.BotLauncher;
import org.powerbot.util.HttpUtils;

public class Boot {
	private static Instrumentation instrumentation;
	private static File self;

	public static void premain(final String agentArgs, final Instrumentation instrumentation) throws IOException {
		Boot.instrumentation = instrumentation;
		main(new String[0]);
	}

	@SuppressWarnings("unused")
	public static void agentmain(final String agentArgs, final Instrumentation instrumentation) throws IOException {
		premain(agentArgs, instrumentation);
	}

	public static void main(final String[] args) throws IOException {
		if (Configuration.OS == OperatingSystem.MAC) {
			System.setProperty("apple.awt.UIElement", "true");
		}

		self = new File(Boot.class.getProtectionDomain().getCodeSource().getLocation().getPath());

		for (final String arg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
			if (arg.toLowerCase().startsWith("-javaagent:") && !arg.equalsIgnoreCase("-javaagent:" + self)) {
				return;
			}
		}

		final Logger logger = Logger.getLogger("");
		for (final Handler handler : logger.getHandlers()) {
			logger.removeHandler(handler);
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
				std.print(record.getLoggerName());
				std.print(": ");
				std.print(text);
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
				Logger.getLogger("main").logp(Level.SEVERE, t.getStackTrace()[1].getClassName(), t.getStackTrace()[1].getMethodName(), e.getMessage(), e);
				e.printStackTrace();
			}
		});

		if (Configuration.OS == OperatingSystem.MAC) {
			System.setProperty("apple.laf.useScreenMenuBar", "true");
		}
		System.setProperty("http.keepalive", "false");

		final Sandbox sandbox = new Sandbox();
		sandbox.checkCreateClassLoader();
		//System.setSecurityManager(sandbox);

		final boolean agent = System.getProperty("bot.agent", "true").equals("true") && self.isFile();

		final String config = "com.jagex.config", os = "oldschool";
		if (System.getProperty(config, "").isEmpty()) {
			String mode = System.getProperty(Configuration.URLs.GAME_VERSION_KEY, "").toLowerCase();
			mode = mode.equals(os) || mode.equals("os") ? os : "www";
			System.setProperty(config, "http://" + mode + "." + Configuration.URLs.GAME + "/k=3/l=" + System.getProperty("user.language", "en") + "/jav_config.ws");
		}
		System.clearProperty(Configuration.URLs.GAME_VERSION_KEY);

		final URL src = new URL("http://www." + Configuration.URLs.GAME + "/downloads/jagexappletviewer.jar");
		final String[] name = {src.getFile().substring(src.getFile().lastIndexOf('/') + 1), ""};
		name[1] = name[0].substring(0, name[0].indexOf('.'));
		final File jar = new File(Configuration.HOME, name[0]);
		if (!jar.exists() || jar.lastModified() < System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000) {
			HttpUtils.download(src, jar);
		}

		if (agent && instrumentation == null) {
			final String[] cmd = {"java", "-Xmx512m", "-Xss2m", "-XX:+UseConcMarkSweepGC", "-Dsun.java2d.noddraw=true",
					"-D" + config + "=" + System.getProperty(config, ""), "-D",
					"-javaagent:" + self.getAbsolutePath(), "-classpath", jar.getAbsolutePath(), name[1], ""};

			if (Configuration.OS == OperatingSystem.MAC) {
				cmd[6] = "-Xdock:name=" + Configuration.NAME;
			}

			Runtime.getRuntime().exec(cmd, new String[0]);
			return;
		}

		if (Configuration.OS == OperatingSystem.MAC) {
			System.setProperty("apple.awt.UIElement", "false");
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (final Exception ignored) {
				}
			}
		});

		new Thread(new BotLauncher()).start();

		if (!agent) {
			logger.warning("Not using instrumentation agent - higher risk of detection");

			try {
				final Method m = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
				m.setAccessible(true);
				m.invoke(ClassLoader.getSystemClassLoader(), jar.toURI().toURL());

				final Object o = Class.forName(name[1]).newInstance();
				o.getClass().getMethod("main", new Class[]{String[].class}).invoke(o, new Object[]{new String[]{""}});
			} catch (final Exception e) {
				throw new IOException(e);
			}
		}
	}

	public static void fork() {
		if (self == null) {
			return;
		}

		final String k = Configuration.URLs.GAME_VERSION_KEY;
		final String[] cmd = {"java", "-D" + k + "=" + System.getProperty(k, ""), "-classpath", self.getAbsolutePath(), Boot.class.getCanonicalName()};
		try {
			Runtime.getRuntime().exec(cmd, new String[0]);
		} catch (final IOException ignored) {
		}
	}
}
