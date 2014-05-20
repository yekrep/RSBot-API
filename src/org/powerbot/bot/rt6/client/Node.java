package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class Node extends ReflectProxy {
	public Node(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public Node getNext() {
		return new Node(reflector, reflector.access(this));
	}

	public long getId() {
		return reflector.accessLong(this);
	}
}
