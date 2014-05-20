package org.powerbot.bot.rt6.client;

import org.powerbot.bot.Reflector;

public class RSProjectile extends RSInteractable {
	public RSProjectile(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getID() {
		return engine.accessInt(this);
	}

	public int getTargetID() {
		return engine.accessInt(this);
	}
}
