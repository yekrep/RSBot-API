package org.powerbot.bot.rt4.client;

public interface ItemConfig extends Node {
	String getName();

	boolean isMembers();

	String[] getActions1();

	String[] getActions2();
}
