package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class NodeListCache extends ReflectProxy {
	public NodeListCache(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public NodeDeque getNodeList() {
		return new NodeDeque(reflector, reflector.access(this));
	}
}
