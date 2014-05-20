package org.powerbot.bot.rt4.client;

import org.powerbot.bot.reflect.ReflectionEngine;

public class Npc extends Actor {
	public Npc(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public NpcConfig getConfig() {
		return new NpcConfig(engine, engine.access(this));
	}
}
