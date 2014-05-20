package org.powerbot.bot.rt6.client;

import org.powerbot.bot.reflect.ReflectionEngine;

public class RSProjectileNode extends Node {
	public RSProjectileNode(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public RSProjectile getProjectile() {
		return new RSProjectile(engine, engine.access(this));
	}
}
