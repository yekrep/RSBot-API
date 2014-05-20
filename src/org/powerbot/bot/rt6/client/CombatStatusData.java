package org.powerbot.bot.rt6.client;

import org.powerbot.bot.reflect.ReflectionEngine;

public class CombatStatusData extends LinkedListNode {
	public CombatStatusData(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public int getLoopCycleStatus() {
		return engine.accessInt(this);
	}

	public int getHPRatio() {
		return engine.accessInt(this);
	}
}
