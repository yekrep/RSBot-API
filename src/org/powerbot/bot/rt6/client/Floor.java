package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class Floor extends ReflectProxy {
	public Floor(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int[][] getHeights() {
		return reflector.access(this, int[][].class);
	}
}
