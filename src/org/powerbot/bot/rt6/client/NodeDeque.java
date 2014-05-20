package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class NodeDeque extends ReflectProxy {
	public NodeDeque(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public Node getTail() {
		return new Node(reflector, reflector.access(this));
	}
}
