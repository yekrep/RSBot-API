package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.ReflectionEngine;

public class RSAnimableNode extends ContextAccessor {
	public RSAnimableNode(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public RSAnimableNode getNext() {
		return new RSAnimableNode(engine, engine.access(this));
	}

	public RSObject getRSAnimable() {
		return new RSObject(engine, engine.access(this));
	}
}
