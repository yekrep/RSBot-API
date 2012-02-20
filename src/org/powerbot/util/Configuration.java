package org.powerbot.util;

public class Configuration {
	public static final String NAME = "RSBot";
	private static final int VERSION = 4000;

	public interface Paths {
		public interface URLs {
			public static final String GAME = "runescape.com";
		}
	}

	public static int getVersion() {
		return VERSION;
	}

	public static String getVersionFormatted() {
		return StringUtil.formatVersion(getVersion());
	}
}
