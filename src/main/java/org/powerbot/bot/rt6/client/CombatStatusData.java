package org.powerbot.bot.rt6.client;

import org.powerbot.bot.Reflector;

public class CombatStatusData extends LinkedListNode {
	private static final Reflector.FieldCache a = new Reflector.FieldCache(),
			b = new Reflector.FieldCache();

	public CombatStatusData(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getCycleEnd() {
		return reflector.accessInt(this, a);
	}

	public int getHealthRatio() {
		return reflector.accessInt(this, b);
	}
}
