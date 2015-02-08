package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class FloorSettings extends ReflectProxy {
	private static final Reflector.FieldCache a = new Reflector.FieldCache();

	public FloorSettings(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public byte[][][] getBytes() {
		return reflector.access(this, a, byte[][][].class);
	}
}
