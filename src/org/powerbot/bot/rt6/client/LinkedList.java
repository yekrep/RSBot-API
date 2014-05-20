package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.Reflector;

public class LinkedList extends ContextAccessor {
	public LinkedList(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public LinkedListNode getTail() {
		return new LinkedListNode(engine, engine.access(this));
	}
}
