package org.powerbot.bot.rt4.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class Node extends ReflectProxy {
	private static final Reflector.FieldCache a = new Reflector.FieldCache(),
			b = new Reflector.FieldCache(),
			c = new Reflector.FieldCache();

	public Node(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public Node getNext() {
		return new Node(reflector, reflector.access(this, a));
	}

	public Node getPrevious() {
		return new Node(reflector, reflector.access(this, c));
	}

	public long getId() {
		return reflector.accessLong(this, b);
	}
}
