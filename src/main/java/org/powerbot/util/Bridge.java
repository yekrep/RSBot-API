package org.powerbot.util;

import org.powerbot.bot.ContextClassLoader;
import org.powerbot.script.ClientContext;

public class Bridge {

	public static ClientContext ctx() {
		return ((ContextClassLoader) Thread.currentThread().getContextClassLoader()).ctx();
	}

	public static String prop(final String k) {
		return ctx().properties.getProperty(k, "");
	}
}
