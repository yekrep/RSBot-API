package org.powerbot.bot.rt4.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class CollisionMap extends ReflectProxy {
	public CollisionMap(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int[][] getFlags() {
		return reflector.access(this, int[][].class);
	}

	public int getOffsetX() {
		return reflector.accessInt(this);
	}

	public int getOffsetY() {
		return reflector.accessInt(this);
	}
}
