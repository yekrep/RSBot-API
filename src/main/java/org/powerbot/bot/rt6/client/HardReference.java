package org.powerbot.bot.rt6.client;

import org.powerbot.bot.*;

public class HardReference extends ReflectProxy {
	private static final Reflector.FieldCache a = new Reflector.FieldCache();

	public HardReference(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public Object get() {
		return reflector.access(this, a);
	}
}
