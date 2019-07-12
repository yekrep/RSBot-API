package org.powerbot.bot.rt6.client;

import org.powerbot.bot.*;

public class NodeListCache extends ReflectProxy {
	private static final Reflector.FieldCache a = new Reflector.FieldCache();

	public NodeListCache(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public NodeDeque getDeque() {
		return new NodeDeque(reflector, reflector.access(this, a));
	}
}
