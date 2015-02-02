package org.powerbot.bot.rt6.client;

import org.powerbot.bot.Reflector;

public class Projectile extends Entity {
	public Projectile(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getId() {
		return reflector.accessInt(this);
	}

	public int getTargetUid() {
		return reflector.accessInt(this);
	}
}
