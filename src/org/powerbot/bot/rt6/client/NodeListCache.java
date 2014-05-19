package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.ReflectionEngine;

public class NodeListCache extends ContextAccessor {
	public NodeListCache(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public NodeDeque getNodeList() {
		return new NodeDeque(engine, engine.access(this));
	}
}
