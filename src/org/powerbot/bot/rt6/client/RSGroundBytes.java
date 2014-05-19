package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.ReflectionEngine;

public class RSGroundBytes extends ContextAccessor {
	public RSGroundBytes(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public byte[][][] getBytes() {
		return engine.access(this, byte[][][].class);
	}
}
