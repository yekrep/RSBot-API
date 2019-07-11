package org.powerbot.util;

public class Environment {
	public static final String NAME = "RSBot";
	public static final int VERSION = 7102;
	public static final OperatingSystem OS;
	public static final boolean JRE6;
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
	}
}
