package org.powerbot.bot.rt6.client;

import org.powerbot.bot.*;

public class SoftReference extends ReflectProxy {
	private static final Reflector.FieldCache a = new Reflector.FieldCache();

	public SoftReference(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public Object get() {
		return reflector.access(this, a);
	}
}
