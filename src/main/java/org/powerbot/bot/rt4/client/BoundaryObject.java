package org.powerbot.bot.rt4.client;

import org.powerbot.bot.*;

public class BoundaryObject extends ReflectProxy {
	private static final Reflector.FieldCache a = new Reflector.FieldCache(),
			b = new Reflector.FieldCache();

	public BoundaryObject(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public long getUid() {
		return reflector.accessLong(this, a);
	}

	public int getMeta() {
		return reflector.accessInt(this, b);
	}
}