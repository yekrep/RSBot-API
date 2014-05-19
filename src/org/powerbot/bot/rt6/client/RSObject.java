package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectionEngine;

public class RSObject extends RSInteractable {
	public RSObject(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public int getId() {
		return engine.accessInt(this);
	}

	public AbstractModel getModel() {
		return new AbstractModel(engine, engine.access(this));
	}
}
