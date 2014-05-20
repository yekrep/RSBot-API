package org.powerbot.bot.rt6.client;

import org.powerbot.bot.reflect.ReflectionEngine;

public class RSObject extends RSInteractable {
	public RSObject(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public int getId() {
		return engine.accessInt(this);
	}
}
