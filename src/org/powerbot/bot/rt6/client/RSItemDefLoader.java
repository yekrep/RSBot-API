package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.Reflector;

public class RSItemDefLoader extends ContextAccessor {
	public RSItemDefLoader(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public Cache getCache() {
		return new Cache(engine, engine.access(this));
	}

	public Cache getModelCache() {
		return new Cache(engine, engine.access(this));
	}

	public boolean isMembers() {
		return engine.accessBool(this);
	}
}
