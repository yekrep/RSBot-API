package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.ReflectionEngine;

public class LinkedListNode extends ContextAccessor {
	public LinkedListNode(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public LinkedListNode getNext(){
		return new LinkedListNode(engine, engine.access(this));
	}
}
