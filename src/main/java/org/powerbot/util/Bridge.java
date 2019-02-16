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
