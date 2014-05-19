package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.ReflectionEngine;

public class RSMessageData extends ContextAccessor {
	public RSMessageData(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public String getMessage() {
		return engine.access(this, String.class);
	}
}
