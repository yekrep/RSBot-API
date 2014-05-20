package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.Reflector;

public class Sequence extends ContextAccessor {
	public Sequence(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getID() {
		return engine.accessInt(this);
	}
}
