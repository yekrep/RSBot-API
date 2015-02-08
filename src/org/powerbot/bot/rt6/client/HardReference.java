package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class HardReference extends ReflectProxy {
	private static final Reflector.FieldCache a = new Reflector.FieldCache();

	public HardReference(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public Object get() {
		return reflector.access(this, a);
	}
}
