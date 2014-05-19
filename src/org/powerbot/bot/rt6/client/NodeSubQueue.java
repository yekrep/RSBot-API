package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.ReflectionEngine;

public class NodeSubQueue extends ContextAccessor {
	public NodeSubQueue(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public NodeSub getTail() {
		return new NodeSub(engine, engine.access(this));
	}
}
