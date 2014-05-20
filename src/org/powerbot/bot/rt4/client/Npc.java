package org.powerbot.bot.rt4.client;

import org.powerbot.bot.Reflector;

public class Npc extends Actor {
	public Npc(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public NpcConfig getConfig() {
		return new NpcConfig(reflector, reflector.access(this));
	}
}
