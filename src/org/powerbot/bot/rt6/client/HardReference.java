package org.powerbot.bot.rt6.client;

import org.powerbot.bot.reflect.ContextAccessor;
import org.powerbot.bot.reflect.ReflectionEngine;

public class HardReference extends ContextAccessor {
	public HardReference(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public Object get() {
		return engine.access(this);
	}
}
