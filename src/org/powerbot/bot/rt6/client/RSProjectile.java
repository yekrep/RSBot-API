package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectionEngine;

public class RSProjectile extends RSInteractable {
	public RSProjectile(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public int getID() {
		return engine.accessInt(this);
	}

	public int getTargetID() {
		return engine.accessInt(this);
	}
}
