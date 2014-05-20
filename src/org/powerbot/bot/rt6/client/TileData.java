package org.powerbot.bot.rt6.client;

import org.powerbot.bot.reflect.ContextAccessor;
import org.powerbot.bot.reflect.ReflectionEngine;

public class TileData extends ContextAccessor {
	public TileData(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public int[][] getHeights() {
		return engine.access(this, int[][].class);
	}
}
