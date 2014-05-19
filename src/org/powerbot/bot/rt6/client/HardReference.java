package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.ReflectionEngine;

public class HardReference extends ContextAccessor {
	public HardReference(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public Object get() {
		return engine.access(this);
	}
}
