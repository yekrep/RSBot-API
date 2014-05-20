package org.powerbot.bot.rt6.client;

import org.powerbot.bot.Reflector;

public class CombatStatusData extends LinkedListNode {
	public CombatStatusData(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getLoopCycleStatus() {
		return engine.accessInt(this);
	}

	public int getHPRatio() {
		return engine.accessInt(this);
	}
}
