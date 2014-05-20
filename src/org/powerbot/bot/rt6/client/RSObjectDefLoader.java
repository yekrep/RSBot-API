package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.Reflector;

public class RSObjectDefLoader extends ContextAccessor {
	public RSObjectDefLoader(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public Cache getCache() {
		return new Cache(engine, engine.access(this));
	}
}
