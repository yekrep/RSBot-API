package org.powerbot.util;

import org.powerbot.bot.ContextClassLoader;
import org.powerbot.script.ClientContext;
import org.powerbot.script.Input;

public class Bridge {

	public static ContextClassLoader cl() {
		return (ContextClassLoader) Thread.currentThread().getContextClassLoader();
	}

	public static ClientContext ctx() {
		return cl().ctx();
	}

	public static String prop(final String k) {
		return ctx().properties.getProperty(k, "");
	}
}
