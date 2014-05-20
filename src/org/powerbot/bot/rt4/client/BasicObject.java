package org.powerbot.bot.rt4.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class BasicObject extends ReflectProxy {
	public BasicObject(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getUid() {
		return reflector.accessInt(this);
	}

	public int getMeta() {
		return reflector.accessInt(this);
	}
}
