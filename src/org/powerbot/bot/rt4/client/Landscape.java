package org.powerbot.bot.rt4.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.ReflectionEngine;

public class Landscape extends ContextAccessor {
	public Landscape(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public Tile[][][] getTiles() {
		return engine.access(this, Tile[][][].class);
	}
}
