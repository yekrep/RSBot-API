package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectionEngine;

public class CombatStatus extends LinkedListNode {
	public CombatStatus(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public LinkedList getData() {
		return new LinkedList(engine, engine.access(this));
	}
}
