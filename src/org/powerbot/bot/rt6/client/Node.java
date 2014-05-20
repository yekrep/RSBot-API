package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.Reflector;

public class Node extends ContextAccessor {
	public Node(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public Node getNext() {
		return new Node(engine, engine.access(this));
	}

	public long getId() {
		return engine.accessLong(this);
	}
}
