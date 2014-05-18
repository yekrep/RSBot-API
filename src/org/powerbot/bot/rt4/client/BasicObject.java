package org.powerbot.bot.rt4.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.ReflectionEngine;

public class BasicObject extends ContextAccessor {
	public BasicObject(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public int getUid() {
		return engine.accessInt(this);
	}

	public int getMeta() {
		return engine.accessInt(this);
	}
}
