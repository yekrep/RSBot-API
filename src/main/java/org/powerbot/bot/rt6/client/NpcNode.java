package org.powerbot.bot.rt6.client;

import org.powerbot.bot.*;

public class NpcNode extends Node {
	private static final Reflector.FieldCache a = new Reflector.FieldCache();

	public NpcNode(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public Npc getNpc() {
		return new Npc(reflector, reflector.access(this, a));
	}
}
