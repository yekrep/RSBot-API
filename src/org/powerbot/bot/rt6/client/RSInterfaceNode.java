package org.powerbot.bot.rt6.client;

import org.powerbot.bot.reflect.ReflectionEngine;

public class RSInterfaceNode extends Node {
	public RSInterfaceNode(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public int getMainID() {
		return engine.accessInt(this);
	}
}
