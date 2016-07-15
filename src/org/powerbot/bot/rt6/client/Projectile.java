package org.powerbot.bot.rt6.client;

import org.powerbot.bot.Reflector;

public class Projectile extends Entity {
	private static final Reflector.FieldCache a = new Reflector.FieldCache(),
			b = new Reflector.FieldCache();

	public Projectile(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getId() {
		return reflector.accessInt(this, a);
	}

	public int getTargetUid() {
		return reflector.accessInt(this, b);
	}
}
