package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class NodeSubQueue extends ReflectProxy {
	private static final Reflector.FieldCache a = new Reflector.FieldCache();

	public NodeSubQueue(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public NodeSub getSentinel() {
		return new NodeSub(reflector, reflector.access(this, a));
	}
}
