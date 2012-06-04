package org.powerbot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.nio.channels.FileLock;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.powerbot.gui.BotChrome;
import org.powerbot.gui.component.BotLocale;
import org.powerbot.util.Configuration;
import org.powerbot.util.Configuration.OperatingSystem;
import org.powerbot.util.RestrictedSecurityManager;
import org.powerbot.util.StringUtil;
import org.powerbot.util.io.CryptFile;
import org.powerbot.util.io.PrintStreamHandler;

public class Boot implements Runnable {
	private final static Logger log = Logger.getLogger(Boot.class.getName());
	private static volatile boolean released = false;
	private static Runnable releaseLock;

	public static void main(final String[] args) {
		final Logger logger = Logger.getLogger("");
		for (final Handler handler : logger.getHandlers()) {
			logger.removeHandler(handler);
		}
		logger.addHandler(new PrintStreamHandler());

		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				log.logp(Level.SEVERE, t.getStackTrace()[1].getClassName(), t.getStackTrace()[1].getMethodName(), e.getMessage(), e);
				e.printStackTrace();
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

		final int req = Configuration.DEVMODE ? -1 : 768;
		long mem = Runtime.getRuntime().maxMemory() / 1024 / 1024;

		if (mem < req && !restarted) {
			log.severe(String.format("Default heap size of %sm too small, restarting with %sm", mem, req));
			String location = Boot.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			location = StringUtil.urlDecode(location).replaceAll("\\\\", "/");
			final String cmd = "java -Xss6m -Xmx" + req + "m -classpath \"" + location + "\" \"" + Boot.class.getCanonicalName() + "\" " + SWITCH_RESTARTED;
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

		boolean locked = false;

		try {
			final RandomAccessFile tmpraf = new RandomAccessFile(Configuration.LOCK, "rw");
			final FileLock tmplock = tmpraf.getChannel().tryLock();
			locked = tmplock != null;
			releaseLock = new Runnable() {
				@Override
				public void run() {
					try {
						if (released) {
							return;
						}
						if (tmplock != null) {
							tmplock.release();
						}
						if (tmpraf != null) {
							tmpraf.close();
						}
					} catch (final IOException ignored) {
					}
					Configuration.LOCK.delete();
					released = true;
				}
			};
			Runtime.getRuntime().addShutdownHook(new Thread(releaseLock));
		} catch (final IOException ignored) {
		}

		if (!locked) {
			final String msg = "An instance of " + Configuration.NAME + " is already running";
			log.severe(msg);
			if (!Configuration.DEVMODE && Configuration.OS == OperatingSystem.WINDOWS) {
				JOptionPane.showMessageDialog(null, msg, BotLocale.ERROR, JOptionPane.ERROR_MESSAGE);
			}
			return;
		}

		try {
			logger.addHandler(new PrintStreamHandler(new File(new File(System.getProperty("java.io.tmpdir")), Configuration.NAME + ".log")));
		} catch (final FileNotFoundException ignored) {
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

		StringUtil.newStringUtf8(null); // prevents ClassCircularityError exceptions
		CryptFile.PERMISSIONS.clear();
		System.setSecurityManager(new RestrictedSecurityManager());
		System.setProperty("java.net.preferIPv4Stack", "true");
		System.setProperty("sun.net.spi.nameservice.nameservers", RestrictedSecurityManager.DNS1 + "," + RestrictedSecurityManager.DNS2);
		System.setProperty("sun.net.spi.nameservice.provider.1", "dns,sun");

		BotChrome.getInstance();
	}

	public void run() {
		main(new String[]{});
	}

	public static void releaseLock() {
		releaseLock.run();
	}
}
