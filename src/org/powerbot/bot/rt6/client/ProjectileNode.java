package org.powerbot.bot.rt6.client;

import org.powerbot.bot.Reflector;

public class ProjectileNode extends Node {
	public ProjectileNode(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public Projectile getProjectile() {
		return new Projectile(reflector, reflector.access(this));
	}
}
