package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class FloorSettings extends ReflectProxy {
	public FloorSettings(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public byte[][][] getBytes() {
		return reflector.access(this, byte[][][].class);
	}
}
