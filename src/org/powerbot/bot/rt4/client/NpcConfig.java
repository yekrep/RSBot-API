package org.powerbot.bot.rt4.client;

public interface NpcConfig {
	int getId();

	int getLevel();

	String getName();

	String[] getActions();

	int[] getConfigs();

	int getVarpbitIndex();

	int getVarbit();
}
