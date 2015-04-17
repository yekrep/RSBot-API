package org.powerbot.bot.rt4.client;

public interface Actor {
	int getX();

	int getZ();

	int getHeight();

	int getAnimation();

	int getSpeed();

	int getCurrentHealth();

	int getMaxHealth();

	int getCycleEnd();

	String getOverheadMessage();

	int getOrientation();

	int getInteractingIndex();
}
