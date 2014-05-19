package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.ReflectionEngine;

public class Settings extends ContextAccessor {
	public Settings(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public int[] getData() {
		return engine.access(this, int[].class);
	}
}
