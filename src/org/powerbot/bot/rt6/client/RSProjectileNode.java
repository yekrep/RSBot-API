package org.powerbot.bot.rt6.client;

import org.powerbot.bot.Reflector;

public class RSProjectileNode extends Node {
	public RSProjectileNode(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public RSProjectile getProjectile() {
		return new RSProjectile(reflector, reflector.access(this));
	}
}
