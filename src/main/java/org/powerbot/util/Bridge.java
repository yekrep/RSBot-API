package org.powerbot.util;

import org.powerbot.bot.ContextClassLoader;
import org.powerbot.script.ClientContext;
import org.powerbot.script.Input;

import java.util.HashMap;
import java.util.Map;

public class Bridge {
	private static final Map<String, String> v;

	static {
		v = new HashMap<>();
		v.put("name", "RSBot");
		v.put("version", "7088");

		final String osv;
		final String os = System.getProperty("os.name");
		if (os.contains("Mac")) {
			osv = "0";
		} else if (os.contains("Windows")) {
			osv = "1";
		} else if (os.contains("Linux")) {
			osv = "2";
		} else {
			osv = "3";
		}
		v.put("os", osv);

		v.put("urls.game", "runescape.com");
	}

	public static ContextClassLoader cl() {
		return (ContextClassLoader) Thread.currentThread().getContextClassLoader();
	}

	public static ClientContext ctx() {
		return cl().ctx();
	}

	public static String prop(final String k) {
		return v.containsKey(k) ? v.get(k) : ctx().properties.getProperty(k, "");
	}
}
