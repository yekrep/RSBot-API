package org.powerbot.bot.rs3.client;

public interface RSNPCDef {
	public int getID();

	public String[] getActions();

	public String getName();

	public int getLevel();

	public int[] getOverheadArray1();

	public short[] getOverheadArray2();
}
