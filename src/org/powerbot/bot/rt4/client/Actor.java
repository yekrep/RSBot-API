package org.powerbot.bot.rt4.client;

public interface Actor {
	public int getX();

	public int getZ();

	public int getHeight();

	public int getAnimation();

	public int getSpeed();

	public int getCurrentHealth();

	public int getMaxHealth();

	public int getCycleEnd();

	public String getOverheadMessage();

	public int getOrientation();

	public int getInteractingIndex();
}
