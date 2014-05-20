package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.Reflector;

public class OverheadSprites extends ContextAccessor {
	public OverheadSprites(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int[] getArray1() {
		return engine.accessInts(this);
	}

	public short[] getArray2() {
		return engine.access(this, short[].class);
	}
}
