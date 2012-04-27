package org.powerbot.util;

import java.io.File;

import org.powerbot.util.io.Resources;

/**
 * @author Paris
 */
public class Configuration {
	public static final String NAME = "RSBot";
	public static final boolean FROMJAR;
	public static boolean DEVMODE = false;
	public static final int VERSION = 4006;
	public static final String STORE, BOOTSETTINGS;
	public static final OperatingSystem OS;

	public enum OperatingSystem {
		MAC, WINDOWS, LINUX, UNKNOWN
	}

	public interface URLs {
		public static final String DOMAIN = "powerbot.org";
		public static final String CONTROL = "http://links." + DOMAIN + "/control";

		public static final String GAME = "runescape.com";
	}

	static {
		FROMJAR = Configuration.class.getClassLoader().getResource(Resources.Paths.ICON) != null;

		final String appdata = System.getenv("APPDATA"), home = System.getProperty("user.home");
		final String root = appdata != null && new File(appdata).isDirectory() ? appdata : home == null ? "~" : home;
		STORE = root + File.separator + NAME + ".db";

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

		BOOTSETTINGS = OS == OperatingSystem.WINDOWS ? root + File.separator + NAME + ".ini" : home + File.separator + "." + NAME.toLowerCase();
	}
}
