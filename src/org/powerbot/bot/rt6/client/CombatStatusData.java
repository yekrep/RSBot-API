package org.powerbot.bot.rt6.client;

import org.powerbot.bot.Reflector;

public class CombatStatusData extends LinkedListNode {
	public CombatStatusData(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getLoopCycleStatus() {
		return reflector.accessInt(this);
	}

	public int getHPRatio() {
		return reflector.accessInt(this);
	}
}
