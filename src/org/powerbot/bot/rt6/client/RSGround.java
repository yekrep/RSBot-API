package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.Reflector;

public class RSGround extends ContextAccessor {
	public RSGround(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public RSAnimableNode getRSAnimableList() {
		return new RSAnimableNode(engine, engine.access(this));
	}

	public RSObject getBoundary1() {
		return new RSObject(engine, engine.access(this));
	}

	public RSObject getBoundary2() {
		return new RSObject(engine, engine.access(this));
	}

	public RSObject getWallDecoration1() {
		return new RSObject(engine, engine.access(this));
	}

	public RSObject getWallDecoration2() {
		return new RSObject(engine, engine.access(this));
	}

	public RSObject getFloorDecoration() {
		return new RSObject(engine, engine.access(this));
	}

	public RSItemPile getRSItemPile() {
		return new RSItemPile(engine, engine.access(this));
	}
}
