package org.powerbot.script.xenon;

import java.util.Properties;

public class Environment {
	private static final Properties properties = new Properties();

	public static String getDisplayName() {
		return properties.getProperty("user.name");
	}

	public static int getUserId() {
		final String s = properties.getProperty("user.id");
		return s == null || s.isEmpty() ? -1 : Integer.parseInt(s);
	}

	public static Properties getProperties() {
		return properties;
	}
}
