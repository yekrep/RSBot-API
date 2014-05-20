package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.Reflector;

public class SoftReference extends ContextAccessor {
	public SoftReference(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public Object get() {
		return engine.access(this);
	}
}
