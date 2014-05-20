package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class RSMessageData extends ReflectProxy {
	public RSMessageData(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public String getMessage() {
		return reflector.accessString(this);
	}
}
