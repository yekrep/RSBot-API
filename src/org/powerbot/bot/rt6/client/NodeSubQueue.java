package org.powerbot.bot.rt6.client;

import org.powerbot.bot.reflect.ContextAccessor;
import org.powerbot.bot.reflect.ReflectionEngine;

public class NodeSubQueue extends ContextAccessor {
	public NodeSubQueue(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public NodeSub getTail() {
		return new NodeSub(engine, engine.access(this));
	}
}
