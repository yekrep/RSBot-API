package org.powerbot.bot.rt6.client;

import org.powerbot.bot.Reflector;

public class NodeSub extends Node {
	private static final Reflector.FieldCache a = new Reflector.FieldCache();

	public NodeSub(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public NodeSub getNextSub() {
		return new NodeSub(reflector, reflector.access(this, a));
	}
}
