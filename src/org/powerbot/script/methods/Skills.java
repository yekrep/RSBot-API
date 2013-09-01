package org.powerbot.script.methods;

import org.powerbot.client.Client;
import org.powerbot.client.PlayerMetaInfo;
import org.powerbot.client.Skill;

import static org.powerbot.script.util.Constants.getInt;
import static org.powerbot.script.util.Constants.getIntA;

public class Skills extends MethodProvider {
	public static final int[] XP_TABLE = getIntA("skills.xp.table");
	public static final int ATTACK = getInt("skills.attack");
	public static final int DEFENSE = getInt("skills.defense");
	public static final int STRENGTH = getInt("skills.strength");
	public static final int CONSTITUTION = getInt("skills.constitution");
	public static final int RANGE = getInt("skills.range");
	public static final int PRAYER = getInt("skills.prayer");
	public static final int MAGIC = getInt("skills.magic");
	public static final int COOKING = getInt("skills.cooking");
	public static final int WOODCUTTING = getInt("skills.woodcutting");
	public static final int FLETCHING = getInt("skills.fletching");
	public static final int FISHING = getInt("skills.fishing");
	public static final int FIREMAKING = getInt("skills.firemaking");
	public static final int CRAFTING = getInt("skills.crafting");
	public static final int SMITHING = getInt("skills.smithing");
	public static final int MINING = getInt("skills.mining");
	public static final int HERBLORE = getInt("skills.herblore");
	public static final int AGILITY = getInt("skills.agility");
	public static final int THIEVING = getInt("skills.thieving");
	public static final int SLAYER = getInt("skills.slayer");
	public static final int FARMING = getInt("skills.farming");
	public static final int RUNECRAFTING = getInt("skills.runecrafting");
	public static final int HUNTER = getInt("skills.hunter");
	public static final int CONSTRUCTION = getInt("skills.construction");
	public static final int SUMMONING = getInt("skills.summoning");
	public static final int DUNGEONEERING = getInt("skills.dungeoneering");
	public static final int DIVINATION = getInt("skills.divination");

	public Skills(MethodContext factory) {
		super(factory);
	}

	public int getLevel(final int index) {
		final int[] levels = getLevels();
		if (index >= 0 && index < levels.length) {
			return levels[index];
		}
		return -1;
	}

	public int getRealLevel(final int index) {
		final int[] levels = getRealLevels();
		if (index >= 0 && index < levels.length) {
			return levels[index];
		}
		return -1;
	}

	public int getExperience(final int index) {
		final int[] exps = getExperiences();
		if (index >= 0 && index < exps.length) {
			return exps[index];
		}
		return -1;
	}

	public int[] getLevels() {
		Client client = ctx.getClient();
		if (client == null) {
			return new int[0];
		}
		final PlayerMetaInfo info = client.getPlayerMetaInfo();
		final Skill[] skills;
		if (info != null && (skills = info.getSkills()) != null) {
			final int[] levels = new int[skills.length];
			for (int i = 0; i < skills.length; i++) {
				levels[i] = skills[i].getLevel();
			}
			return levels;
		}
		return new int[0];
	}

	public int[] getRealLevels() {
		Client client = ctx.getClient();
		if (client == null) {
			return new int[0];
		}
		final PlayerMetaInfo info = client.getPlayerMetaInfo();
		final Skill[] skills;
		if (info != null && (skills = info.getSkills()) != null) {
			final int[] levels = new int[skills.length];
			for (int i = 0; i < skills.length; i++) {
				levels[i] = skills[i].getRealLevel();
			}
			return levels;
		}
		return new int[0];
	}

	public int[] getExperiences() {
		Client client = ctx.getClient();
		if (client == null) {
			return new int[0];
		}
		final PlayerMetaInfo info = client.getPlayerMetaInfo();
		final Skill[] skills;
		if (info != null && (skills = info.getSkills()) != null) {
			final int[] exps = new int[skills.length];
			for (int i = 0; i < skills.length; i++) {
				exps[i] = skills[i].getExperience();
			}
			return exps;
		}
		return new int[0];
	}

	public int getLevelAt(final int exp) {
		for (int i = XP_TABLE.length - 1; i > 0; i--) {
			if (exp > XP_TABLE[i]) {
				return i;
			}
		}
		return 1;
	}

	public int getExperienceAt(final int level) {
		if (level < 0 || level > 120) {
			return -1;
		}
		return XP_TABLE[level];
	}
}
