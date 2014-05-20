package org.powerbot.bot.rt6.client;

import org.powerbot.bot.Reflector;

public class RSObject extends RSInteractable {
	public RSObject(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getId() {
		return engine.accessInt(this);
	}
}
