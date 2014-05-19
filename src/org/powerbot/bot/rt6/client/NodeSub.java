package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectionEngine;

public class NodeSub extends Node {
	public NodeSub(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public NodeSub getNextSub() {
		return new NodeSub(engine, engine.access(this));
	}
}
