package org.powerbot.bot.rt6.client;

import org.powerbot.bot.reflect.ContextAccessor;
import org.powerbot.bot.reflect.ReflectionEngine;

public class RSRotatableObject extends ContextAccessor {
	public RSRotatableObject(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public int getOrientation() {
		return engine.accessInt(this);
	}

	public int getType() {
		return engine.accessInt(this);
	}
}
