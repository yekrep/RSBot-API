package org.powerbot.bot.rt4.client;

public interface Player extends Actor {
	int getCombatLevel();

	String getName();

	int getTeam();

	PlayerComposite getComposite();
}
