package org.powerbot.bot.rt4.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.Reflector;

public class NodeDeque extends ContextAccessor {
	public NodeDeque(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public Node getSentinel() {
		return new Node(engine, engine.access(this));
	}
}
