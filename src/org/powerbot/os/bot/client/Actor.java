package org.powerbot.os.bot.client;

public interface Actor {
	public int getX();

	public int getZ();

	public int getHeight();

	public int getAnimation();

	public int getSpeed();

	public int getHealthRatio();

	public int getCycleEnd();

	public String getOverheadMessage();

	public int getOrientation();

	public int getInteractingIndex();
}
