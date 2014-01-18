package org.powerbot.os;

/**
 * @author Paris
 */
public class Configuration {
	public static final String NAME = "RSBot OS Beta";
	public static final int VERSION = 1000;

	public static final OperatingSystem OS;

	public enum OperatingSystem {
		MAC, WINDOWS, LINUX, UNKNOWN
	}

	public final class URLs {
		public static final String DOMAIN = "powerbot.org";

		public static final String GAME_DOMAIN = "runescape.com";
	}

	static {
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
	}
}
