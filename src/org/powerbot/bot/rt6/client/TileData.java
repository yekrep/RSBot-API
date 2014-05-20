package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.Reflector;

public class TileData extends ContextAccessor {
	public TileData(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int[][] getHeights() {
		return engine.access(this, int[][].class);
	}
}
