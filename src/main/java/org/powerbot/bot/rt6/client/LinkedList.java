package org.powerbot.bot.rt6.client;

import org.powerbot.bot.*;

public class LinkedList extends ReflectProxy {
	private static final Reflector.FieldCache a = new Reflector.FieldCache();

	public LinkedList(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public LinkedListNode getSentinel() {
		return new LinkedListNode(reflector, reflector.access(this, a));
	}
}
