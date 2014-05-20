package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class RSItemDefLoader extends ReflectProxy {
	public RSItemDefLoader(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public Cache getCache() {
		return new Cache(reflector, reflector.access(this));
	}

	public Cache getModelCache() {
		return new Cache(reflector, reflector.access(this));
	}

	public boolean isMembers() {
		return reflector.accessBool(this);
	}
}
