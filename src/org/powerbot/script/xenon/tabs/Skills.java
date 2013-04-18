package org.powerbot.script.xenon.tabs;

import org.powerbot.bot.Bot;
import org.powerbot.game.client.Client;
import org.powerbot.game.client.PlayerMetaInfo;
import org.powerbot.game.client.Skill;

public class Skills {
	public static final int ATTACK = 0;
	public static final int DEFENSE = 1;
	public static final int STRENGTH = 2;
	public static final int CONSTITUTION = 3;
	public static final int RANGE = 4;
	public static final int PRAYER = 5;
	public static final int MAGIC = 6;
	public static final int COOKING = 7;
	public static final int WOODCUTTING = 8;
	public static final int FLETCHING = 9;
	public static final int FISHING = 10;
	public static final int FIREMAKING = 11;
	public static final int CRAFTING = 12;
	public static final int SMITHING = 13;
	public static final int MINING = 14;
	public static final int HERBLORE = 15;
	public static final int AGILITY = 16;
	public static final int THIEVING = 17;
	public static final int SLAYER = 18;
	public static final int FARMING = 19;
	public static final int RUNECRAFTING = 20;
	public static final int HUNTER = 21;
	public static final int CONSTRUCTION = 22;
	public static final int SUMMONING = 23;
	public static final int DUNGEONEERING = 24;

	public static int getLevel(final int index) {
		final int[] levels = getLevels();
		if (index >= 0 && index < levels.length) return -1;
		return levels[index];
	}

	public static int getRealLevel(final int index) {
		final int[] levels = getRealLevels();
		if (index >= 0 && index < levels.length) return -1;
		return levels[index];
	}

	public static int getExperience(final int index) {
		final int[] exps = getExperiences();
		if (index >= 0 && index < exps.length) return -1;
		return exps[index];
	}

	public static int[] getLevels() {
		final Client client = Bot.client();
		if (client == null) return new int[0];
		final PlayerMetaInfo info = client.getPlayerMetaInfo();
		final Skill[] skills;
		if (info != null && (skills = info.getSkills()) != null) {
			final int[] levels = new int[skills.length];
			for (int i = 0; i < skills.length; i++) levels[i] = skills[i].getLevel();
			return levels;
		}
		return new int[0];
	}

	public static int[] getRealLevels() {
		final Client client = Bot.client();
		if (client == null) return new int[0];
		final PlayerMetaInfo info = client.getPlayerMetaInfo();
		final Skill[] skills;
		if (info != null && (skills = info.getSkills()) != null) {
			final int[] levels = new int[skills.length];
			for (int i = 0; i < skills.length; i++) levels[i] = skills[i].getRealLevel();
			return levels;
		}
		return new int[0];
	}

	public static int[] getExperiences() {
		final Client client = Bot.client();
		if (client == null) return new int[0];
		final PlayerMetaInfo info = client.getPlayerMetaInfo();
		final Skill[] skills;
		if (info != null && (skills = info.getSkills()) != null) {
			final int[] exps = new int[skills.length];
			for (int i = 0; i < skills.length; i++) exps[i] = skills[i].getExperience();
			return exps;
		}
		return new int[0];
	}
}
