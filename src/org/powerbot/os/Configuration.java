package org.powerbot.os;

import java.io.File;

import org.powerbot.os.misc.Resources;

/**
 * @author Paris
 */
public class Configuration {
	public static final String NAME = "RSBot OS Beta";
	public static final int VERSION = 1000;

	public static final boolean FROMJAR;
	public static final File TEMP;

	public static final OperatingSystem OS;

	public enum OperatingSystem {
		MAC, WINDOWS, LINUX, UNKNOWN
	}

	public static final class URLs {
		public static final String DOMAIN = "powerbot.org";

		private static final String SITE_PUBLIC = "http://www." + DOMAIN;
		public static final String LICENSE = SITE_PUBLIC + "/terms/license/";

		public static final String GAME_DOMAIN = "runescape.com";
	}

	static {
		FROMJAR = Configuration.class.getClassLoader().getResource(Resources.Paths.ICON) != null;

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

		TEMP = new File(new File(System.getProperty("java.io.tmpdir")), Integer.toHexString(NAME.hashCode()));
		if (!TEMP.isDirectory()) {
			TEMP.mkdirs();
		}
	}
}
