package org.powerbot.bot.rt6.client;

import org.powerbot.bot.Reflector;

public class ComponentNode extends Node {
	private static final Reflector.FieldCache a = new Reflector.FieldCache();

	public ComponentNode(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getUid() {
		return reflector.accessInt(this, a);
	}
}
