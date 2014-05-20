package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.Reflector;

public class RSMessageData extends ContextAccessor {
	public RSMessageData(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public String getMessage() {
		return engine.accessString(this);
	}
}
