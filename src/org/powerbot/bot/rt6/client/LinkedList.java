package org.powerbot.bot.rt6.client;

import org.powerbot.bot.reflect.ContextAccessor;
import org.powerbot.bot.reflect.ReflectionEngine;

public class LinkedList extends ContextAccessor {
	public LinkedList(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public LinkedListNode getTail() {
		return new LinkedListNode(engine, engine.access(this));
	}
}
