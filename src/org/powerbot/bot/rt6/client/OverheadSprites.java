package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class OverheadSprites extends ReflectProxy {
	public OverheadSprites(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int[] getArray1() {
		return reflector.accessInts(this);
	}

	public short[] getArray2() {
		return reflector.access(this, short[].class);
	}
}
