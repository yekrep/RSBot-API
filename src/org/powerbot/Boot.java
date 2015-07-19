package org.powerbot;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.net.URL;
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
import org.powerbot.misc.CryptFile;
import org.powerbot.util.HttpUtils;
import org.powerbot.util.IOUtils;
import org.powerbot.util.StringUtils;
import org.powerbot.util.TextFormatter;

public class Boot {
	public static Instrumentation instrumentation;
	private static File self;
	public static File icon;

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
			final String ja = "-javaagent:";
			if (arg.toLowerCase().startsWith(ja) && !arg.endsWith("jrebel.jar")) {
				final String path = arg.substring(ja.length());
				if (!path.isEmpty() && !self.getAbsolutePath().endsWith(path)) {
					return;
				}
			}
		}

		if (System.getProperty("os.name").contains("Mac")) {
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty("apple.awt.UIElement", "false");
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
					final String c = x.getClassName();
					if (c.startsWith("java.") || c.startsWith("javax.") || c.startsWith("sun.")) {
						continue;
					}
					s.append(lf).append('\t').append(x.toString());
				}
				Logger.getLogger("").severe(s.toString());
			}
		});

		if (Configuration.OS == OperatingSystem.MAC) {
			System.setProperty("apple.laf.useScreenMenuBar", "true");
		}
		System.setProperty("http.keepalive", "false");

		final Sandbox sandbox = new Sandbox();
		sandbox.checkCreateClassLoader();
		sandbox.checkRead(new File(".").getAbsolutePath());
		System.setSecurityManager(sandbox);

		final String config = "com.jagex.config";
		String v = System.getProperty(config, "");
		if (v.isEmpty() || v.equalsIgnoreCase("rt6") || v.equalsIgnoreCase("rs3")) {
			v = "www";
		} else if (v.equalsIgnoreCase("rt4") || v.equalsIgnoreCase("os")) {
			v = "oldschool";
		}
		if (!v.startsWith("http")) {
			v = "http://" + v + "." + Configuration.URLs.GAME + "/l=" + System.getProperty("user.language", "en") + "/jav_config.ws";
		}
		System.setProperty(config, v);

		final String jag = "jagexappletviewer";
		final URL src = new URL("http://www." + Configuration.URLs.GAME + "/downloads/" + jag + ".jar");
		final String[] name = {src.getFile().substring(src.getFile().lastIndexOf('/') + 1), ""};
		name[1] = name[0].substring(0, name[0].indexOf('.'));
		final File jar = new File(Configuration.HOME, name[0]);
		final long mod = jar.lastModified();
		if (mod <= 0L || mod < System.currentTimeMillis() - 3L * 86400000L) {
			jar.delete();
		}
		if (!jar.isFile()) {
			HttpUtils.download(src, jar);
		}
		IOUtils.write(new ByteArrayInputStream(StringUtils.getBytesUtf8("Language=0\n")), new File(System.getProperty("user.home"), jag + ".preferences"));

		icon = new File(Configuration.TEMP, CryptFile.getHashedName("icon.1.png"));

		if (instrumentation == null) {
			final String[] cmd = {
					"java", "", "",
					"-Dsun.java2d.noddraw=true", "-D" + config + "=" + System.getProperty(config, ""),
					"-Xmx512m", "-Xss2m", "-XX:CompileThreshold=1500", "-Xincgc", "-XX:+UseConcMarkSweepGC", "-XX:+UseParNewGC",
					"-javaagent:" + self.getAbsolutePath(),
					"-classpath", jar.getAbsolutePath(),
					name[1], "runescape"
			};

			if (Configuration.OS == OperatingSystem.MAC) {
				if (icon != null && icon.isFile()) {
					cmd[2] = "-Xdock:icon=" + icon.getAbsolutePath();
				}

				cmd[1] = "-Xdock:name=" + Configuration.NAME;
			} else if (Configuration.OS == OperatingSystem.WINDOWS && System.getProperty("sun.arch.data.model").equals("64")) {
				final String pf = System.getenv("ProgramFiles(x86)");
				final File java;
				if (pf != null && !pf.isEmpty() && (java = new File(pf, "Java")).isDirectory()) {
					File[] rts = java.listFiles();
					rts = rts == null ? new File[0] : rts;
					for (final File jre : rts) {
						final File exe = new File(jre, "bin" + File.separator + "java.exe");
						if (jre.getName().startsWith("jre") && exe.isFile()) {
							cmd[0] = exe.getAbsolutePath();
						}
					}
				}
			}

			final File cwd = new File(System.getProperty("user.home"), "jagexcache" + File.separator + "jagexlauncher" + File.separator + "bin");
			Runtime.getRuntime().exec(cmd, new String[0], cwd.isDirectory() ? cwd : null);
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

		new Thread(new BotChrome()).start();
	}

	public static void fork() {
		if (self == null) {
			return;
		}

		final String k = "com.jagex.config";
		final String[] cmd = {"java", "-D" + k + "=" + System.getProperty(k, ""), "-classpath", self.getAbsolutePath(), Boot.class.getCanonicalName()};
		try {
			Runtime.getRuntime().exec(cmd, new String[0]);
		} catch (final IOException ignored) {
		}
	}
}
