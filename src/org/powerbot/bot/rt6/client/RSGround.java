package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class RSGround extends ReflectProxy {
	public RSGround(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public RSAnimableNode getRSAnimableList() {
		return new RSAnimableNode(reflector, reflector.access(this));
	}

	public RSObject getBoundary1() {
		return new RSObject(reflector, reflector.access(this));
	}

	public RSObject getBoundary2() {
		return new RSObject(reflector, reflector.access(this));
	}

	public RSObject getWallDecoration1() {
		return new RSObject(reflector, reflector.access(this));
	}

	public RSObject getWallDecoration2() {
		return new RSObject(reflector, reflector.access(this));
	}

	public RSObject getFloorDecoration() {
		return new RSObject(reflector, reflector.access(this));
	}

	public RSItemPile getRSItemPile() {
		return new RSItemPile(reflector, reflector.access(this));
	}
}
