package org.powerbot.bot.rt4.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class WallObject extends ReflectProxy {
	private static final Reflector.FieldCache a = new Reflector.FieldCache(),
			b = new Reflector.FieldCache();

	public WallObject(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public long getUid() {
		return reflector.accessLong(this, a);
	}

	public int getMeta() {
		return reflector.accessInt(this, b);
	}
}
