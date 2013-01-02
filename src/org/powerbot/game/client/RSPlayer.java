package org.powerbot.game.client;

public interface RSPlayer extends RSCharacter {
	public int getTeam();

	public RSPlayerComposite getComposite();

	public String getName();

	public int getPrayerIcon();

	public int getSkullIcon();

	public int getLevel();
}
