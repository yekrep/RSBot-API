package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.ReflectionEngine;

public class RSGroundInfo extends ContextAccessor {
	public RSGroundInfo(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public TileData[] getTileData() {
		return engine.access(this, TileData[].class);
	}

	public RSGround[][][] getRSGroundArray() {
		return engine.access(this, RSGround[][][].class);
	}
}
