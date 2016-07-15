package org.powerbot.bot.rt4.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class CollisionMap extends ReflectProxy {
	private static final Reflector.FieldCache a = new Reflector.FieldCache(),
			b = new Reflector.FieldCache(),
			c = new Reflector.FieldCache();

	public CollisionMap(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int[][] getFlags() {
		return reflector.access(this, a, int[][].class);
	}

	public int getOffsetX() {
		return reflector.accessInt(this, b);
	}

	public int getOffsetY() {
		return reflector.accessInt(this, c);
	}
}
