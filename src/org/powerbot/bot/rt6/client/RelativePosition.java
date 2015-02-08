package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class RelativePosition extends ReflectProxy {
	public RelativePosition(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public float getX() {
		return reflector.accessFloat(this);
	}

	public float getY() {
		return reflector.accessFloat(this);
	}

	public float getZ() {
		return reflector.accessFloat(this);
	}
}
