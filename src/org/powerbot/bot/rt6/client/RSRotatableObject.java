package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class RSRotatableObject extends ReflectProxy {
	public RSRotatableObject(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getOrientation() {
		return reflector.accessInt(this);
	}

	public int getType() {
		return reflector.accessInt(this);
	}
}
