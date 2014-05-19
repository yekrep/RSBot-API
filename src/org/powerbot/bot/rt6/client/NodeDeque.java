package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.ReflectionEngine;

public class NodeDeque extends ContextAccessor {
	public NodeDeque(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public Node getTail() {
		return new Node(engine, engine.access(this));
	}
}
