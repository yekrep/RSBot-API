package org.powerbot.bot.rt6.client;

public interface RSPlayer extends RSCharacter {
	int getTeam();

	RSPlayerComposite getComposite();

	String getName();

	int getLevel();

	int[] getOverheadArray1();

	int[] getOverheadArray2();
}
