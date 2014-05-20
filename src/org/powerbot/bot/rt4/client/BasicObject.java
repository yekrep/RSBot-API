package org.powerbot.bot.rt4.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.Reflector;

public class BasicObject extends ContextAccessor {
	public BasicObject(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getUid() {
		return engine.accessInt(this);
	}

	public int getMeta() {
		return engine.accessInt(this);
	}
}
