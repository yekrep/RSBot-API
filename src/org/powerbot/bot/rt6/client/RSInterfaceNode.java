package org.powerbot.bot.rt6.client;

import org.powerbot.bot.Reflector;

public class RSInterfaceNode extends Node {
	public RSInterfaceNode(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getMainID() {
		return reflector.accessInt(this);
	}
}
