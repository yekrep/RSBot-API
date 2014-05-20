package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class RSAnimableNode extends ReflectProxy {
	public RSAnimableNode(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public RSAnimableNode getNext() {
		return new RSAnimableNode(reflector, reflector.access(this));
	}

	public RSObject getRSAnimable() {
		return new RSObject(reflector, reflector.access(this));
	}
}
