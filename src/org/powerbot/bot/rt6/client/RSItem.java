package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class RSItem extends ReflectProxy {
	public RSItem(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getId() {
		return reflector.accessInt(this);
	}

	public int getStackSize() {
		return reflector.accessInt(this);
	}
}
