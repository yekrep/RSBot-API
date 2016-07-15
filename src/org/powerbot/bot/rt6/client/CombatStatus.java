package org.powerbot.bot.rt6.client;

import org.powerbot.bot.Reflector;

public class CombatStatus extends LinkedListNode {
	private static final Reflector.FieldCache a = new Reflector.FieldCache();

	public CombatStatus(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public LinkedList getList() {
		return new LinkedList(reflector, reflector.access(this, a));
	}
}
