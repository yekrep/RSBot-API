package org.powerbot.bot.rt6.client;

import org.powerbot.bot.reflect.ContextAccessor;
import org.powerbot.bot.reflect.ReflectionEngine;

public class OverheadSprites extends ContextAccessor {
	public OverheadSprites(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public int[] getArray1() {
		return engine.access(this, int[].class);
	}

	public short[] getArray2() {
		return engine.access(this, short[].class);
	}
}
