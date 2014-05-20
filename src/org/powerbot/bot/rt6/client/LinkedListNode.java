package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.Reflector;

public class LinkedListNode extends ContextAccessor {
	public LinkedListNode(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public LinkedListNode getNext(){
		return new LinkedListNode(engine, engine.access(this));
	}
}
