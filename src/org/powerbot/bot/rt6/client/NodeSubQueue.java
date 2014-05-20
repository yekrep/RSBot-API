package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.Reflector;

public class NodeSubQueue extends ContextAccessor {
	public NodeSubQueue(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public NodeSub getTail() {
		return new NodeSub(engine, engine.access(this));
	}
}
