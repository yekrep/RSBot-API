package org.powerbot.client;

public interface RSPlayer extends RSCharacter {
	public int getTeam();

	public RSPlayerComposite getComposite();

	public String getName();

	public int getLevel();

	public int[] getOverheadArray1();

	public int[] getOverheadArray2();
}
