package org.powerbot.bot.rt6.client;

import org.powerbot.bot.Reflector;

public class ComponentNode extends Node {
	public ComponentNode(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getUid() {
		return reflector.accessInt(this);
	}
}
