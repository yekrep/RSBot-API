package org.powerbot.bot.rt6.client;

import org.powerbot.bot.Reflector;

public class CombatStatus extends LinkedListNode {
	public CombatStatus(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public LinkedList getData() {
		return new LinkedList(reflector, reflector.access(this));
	}
}
