package org.powerbot.bot.rt4.client;

import org.powerbot.bot.reflect.ContextAccessor;
import org.powerbot.bot.reflect.ReflectionEngine;

public class Node extends ContextAccessor {
	public Node(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public Node getNext() {
		return new Node(engine, engine.access(this));
	}

	public long getId() {
		return engine.accessLong(this);
	}
}
