package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class RSObjectDefLoader extends ReflectProxy {
	public RSObjectDefLoader(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public Cache getCache() {
		return new Cache(reflector, reflector.access(this));
	}
}
