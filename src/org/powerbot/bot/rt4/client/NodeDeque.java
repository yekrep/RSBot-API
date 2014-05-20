package org.powerbot.bot.rt4.client;

import org.powerbot.bot.reflect.ContextAccessor;
import org.powerbot.bot.reflect.ReflectionEngine;

public class NodeDeque extends ContextAccessor {
	public NodeDeque(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public Node getSentinel() {
		return new Node(engine, engine.access(this));
	}
}
