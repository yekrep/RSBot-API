package org.powerbot.util;

import org.powerbot.bot.ContextClassLoader;
import org.powerbot.script.ClientContext;
import org.powerbot.script.Input;

import java.util.HashMap;
import java.util.Map;

public class Bridge {

	public static ContextClassLoader cl() {
		return (ContextClassLoader) Thread.currentThread().getContextClassLoader();
	}

	public static ClientContext ctx() {
		return cl().ctx();
	}
}
