package org.powerbot.util;

public class Environment {
	public static final String NAME = "RSBot";
	public static final int VERSION = 7102;
	public static final OperatingSystem OS;
	public static final String[] DOMAINS = {"powerbot.org", "runescape.com"};

	public enum OperatingSystem {
		MAC, WINDOWS, LINUX, UNKNOWN
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
