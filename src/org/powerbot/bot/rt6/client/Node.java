package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.ReflectionEngine;

public class Node extends ContextAccessor {
	public Node(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public Node getNext() {
		return engine.access(this, Node.class);
	}

	public long getId() {
		return engine.accessLong(this);
	}
}
