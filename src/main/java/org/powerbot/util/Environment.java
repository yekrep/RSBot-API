package org.powerbot.util;

import org.powerbot.bot.ContextClassLoader;
import org.powerbot.script.ClientContext;

import java.io.File;

public class Environment {
	public static final String NAME = "RSBot";
	public static final int VERSION = 7093;
	public static final OperatingSystem OS;
	public static final boolean JRE6;
	public static final File HOME;
	public static final File TEMP;
	public static final String[] DOMAINS = {"powerbot.org", "runescape.com"};

	public enum OperatingSystem {
		MAC, WINDOWS, LINUX, UNKNOWN
	}

	static {
		final String jre = System.getProperty("java.version");
		JRE6 = jre != null && jre.startsWith("1.6");

		final String os = System.getProperty("os.name");
		if (os.contains("Mac")) {
			OS = OperatingSystem.MAC;
		} else if (os.contains("Windows")) {
			OS = OperatingSystem.WINDOWS;
		} else if (os.contains("Linux")) {
			OS = OperatingSystem.LINUX;
		} else {
			OS = OperatingSystem.UNKNOWN;
		}

		if (OS == OperatingSystem.WINDOWS) {
			HOME = new File(System.getenv("APPDATA"), NAME);
		} else {
			final String user = System.getProperty("user.home");
			final File lib = new File(user, "/Library/");
			if (OS == OperatingSystem.MAC && lib.isDirectory()) {
				HOME = new File(lib, NAME);
			} else {
				HOME = new File(System.getProperty("user.home"), "." + NAME.toLowerCase());
			}
		}

		if (!HOME.isDirectory()) {
			//noinspection ResultOfMethodCallIgnored
			HOME.mkdirs();
		}

		TEMP = new File(System.getProperty("java.io.tmpdir"));
	}

	public static ClientContext ctx() {
		return ((ContextClassLoader) Thread.currentThread().getContextClassLoader()).ctx();
	}
}
