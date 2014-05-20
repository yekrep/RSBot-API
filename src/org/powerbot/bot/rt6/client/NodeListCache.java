package org.powerbot.bot.rt6.client;

import org.powerbot.bot.reflect.ContextAccessor;
import org.powerbot.bot.reflect.ReflectionEngine;

public class NodeListCache extends ContextAccessor {
	public NodeListCache(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public NodeDeque getNodeList() {
		return new NodeDeque(engine, engine.access(this));
	}
}
