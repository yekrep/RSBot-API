package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class TileData extends ReflectProxy {
	public TileData(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int[][] getHeights() {
		return reflector.access(this, int[][].class);
	}
}
