package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.ReflectionEngine;

public class BaseInfo extends ContextAccessor {
	public BaseInfo(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public int getX() {
		return engine.accessInt(this);
	}

	public int getY() {
		return engine.accessInt(this);
	}
}
