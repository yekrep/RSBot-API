package org.powerbot.bot.rs3.client;

public interface CombatStatusData extends LinkedListNode {
	public int getLoopCycleStatus();

	public int getHPRatio();
}
