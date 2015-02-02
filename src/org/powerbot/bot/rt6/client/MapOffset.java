package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class MapOffset extends ReflectProxy {
	public MapOffset(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getX() {
		return reflector.accessInt(this);
	}

	public int getY() {
		return reflector.accessInt(this);
	}
}
