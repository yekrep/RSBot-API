package org.powerbot.bot.rt6.client;

import org.powerbot.bot.reflect.ContextAccessor;
import org.powerbot.bot.reflect.ReflectionEngine;

public class RSObjectDefLoader extends ContextAccessor {
	public RSObjectDefLoader(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public Cache getCache() {
		return new Cache(engine, engine.access(this));
	}
}
