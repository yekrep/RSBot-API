package org.powerbot.bot.rt6.client;

import org.powerbot.bot.reflect.ContextAccessor;
import org.powerbot.bot.reflect.ReflectionEngine;

public class RSGroundBytes extends ContextAccessor {
	public RSGroundBytes(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public byte[][][] getBytes() {
		return engine.access(this, byte[][][].class);
	}
}
