package org.powerbot.script.rt6;

import org.powerbot.bot.rt6.client.Client;
import org.powerbot.bot.rt6.client.PlayerMetaInfo;
import org.powerbot.bot.rt6.client.Skill;

public class Skills extends ClientAccessor {
	@Deprecated
	public static final int[] XP_TABLE = Constants.SKILLS_XP;
	@Deprecated
	public static final int ATTACK = Constants.SKILLS_ATTACK;
	@Deprecated
	public static final int DEFENSE = Constants.SKILLS_DEFENSE;
	@Deprecated
	public static final int STRENGTH = Constants.SKILLS_STRENGTH;
	@Deprecated
	public static final int CONSTITUTION = Constants.SKILLS_CONSTITUTION;
	@Deprecated
	public static final int RANGE = Constants.SKILLS_RANGE;
	@Deprecated
	public static final int PRAYER = Constants.SKILLS_PRAYER;
	@Deprecated
	public static final int MAGIC = Constants.SKILLS_MAGIC;
	@Deprecated
	public static final int COOKING = Constants.SKILLS_COOKING;
	@Deprecated
	public static final int WOODCUTTING = Constants.SKILLS_WOODCUTTING;
	@Deprecated
	public static final int FLETCHING = Constants.SKILLS_FLETCHING;
	@Deprecated
	public static final int FISHING = Constants.SKILLS_FISHING;
	@Deprecated
	public static final int FIREMAKING = Constants.SKILLS_FIREMAKING;
	@Deprecated
	public static final int CRAFTING = Constants.SKILLS_CRAFTING;
	@Deprecated
	public static final int SMITHING = Constants.SKILLS_SMITHING;
	@Deprecated
	public static final int MINING = Constants.SKILLS_MINING;
	@Deprecated
	public static final int HERBLORE = Constants.SKILLS_HERBLORE;
	@Deprecated
	public static final int AGILITY = Constants.SKILLS_AGILITY;
	@Deprecated
	public static final int THIEVING = Constants.SKILLS_THIEVING;
	@Deprecated
	public static final int SLAYER = Constants.SKILLS_SLAYER;
	@Deprecated
	public static final int FARMING = Constants.SKILLS_FARMING;
	@Deprecated
	public static final int RUNECRAFTING = Constants.SKILLS_RUNECRAFTING;
	@Deprecated
	public static final int HUNTER = Constants.SKILLS_HUNTER;
	@Deprecated
	public static final int CONSTRUCTION = Constants.SKILLS_CONSTRUCTION;
	@Deprecated
	public static final int SUMMONING = Constants.SKILLS_SUMMONING;
	@Deprecated
	public static final int DUNGEONEERING = Constants.SKILLS_DUNGEONEERING;
	@Deprecated
	public static final int DIVINATION = Constants.SKILLS_DIVINATION;

	public Skills(final ClientContext factory) {
		super(factory);
	}

	/**
	 * Returns the current level of the skill at the provided index.
	 *
	 * @param index the index of the skill
	 * @return the current level at the specified index
	 */
	public int level(final int index) {
		final int[] levels = levels();
		if (index >= 0 && index < levels.length) {
			return levels[index];
		}
		return -1;
	}

	/**
	 * Returns the real level of the skill at the provided index.
	 *
	 * @param index the index of the skill
	 * @return the real level at the specified index
	 */
	public int realLevel(final int index) {
		final int[] levels = realLevels();
		if (index >= 0 && index < levels.length) {
			return levels[index];
		}
		return -1;
	}

	/**
	 * Returns the experience of the skill at the provided index.
	 *
	 * @param index the index of the skill
	 * @return the experience at the specified index
	 */
	public int experience(final int index) {
		final int[] exps = experiences();
		if (index >= 0 && index < exps.length) {
			return exps[index];
		}
		return -1;
	}

	public int[] levels() {
		final Client client = ctx.client();
		if (client == null) {
			return new int[0];
		}
		final PlayerMetaInfo info = client.getPlayerMetaInfo();
		final Skill[] skills;
		if (info != null && (skills = info.getSkills()) != null) {
			final int[] levels = new int[skills.length];
			for (int i = 0; i < skills.length; i++) {
				if (skills[i] == null) {
					return new int[0];
				}
				levels[i] = skills[i].getLevel();
			}
			return levels;
		}
		return new int[0];
	}

	public int[] realLevels() {
		final Client client = ctx.client();
		if (client == null) {
			return new int[0];
		}
		final PlayerMetaInfo info = client.getPlayerMetaInfo();
		final Skill[] skills;
		if (info != null && (skills = info.getSkills()) != null) {
			final int[] levels = new int[skills.length];
			for (int i = 0; i < skills.length; i++) {
				try {
					levels[i] = skills[i].getRealLevel();
				} catch (final NullPointerException ignored) {
				}
			}
			return levels;
		}
		return new int[0];
	}

	public int[] experiences() {
		final Client client = ctx.client();
		if (client == null) {
			return new int[0];
		}
		final PlayerMetaInfo info = client.getPlayerMetaInfo();
		final Skill[] skills;
		if (info != null && (skills = info.getSkills()) != null) {
			final int[] exps = new int[skills.length];
			for (int i = 0; i < skills.length; i++) {
				try {
					exps[i] = skills[i].getExperience();
				} catch (final NullPointerException ignored) {
				}
			}
			return exps;
		}
		return new int[0];
	}

	/**
	 * Determines the level at the specified amount of exp.
	 *
	 * @param exp the exp to convert to level
	 * @return the level with the given amount of exp
	 */
	public int levelAt(final int exp) {
		for (int i = Constants.SKILLS_XP.length - 1; i > 0; i--) {
			if (exp > Constants.SKILLS_XP[i]) {
				return i;
			}
		}
		return 1;
	}

	/**
	 * Determines the experience required for the specified level.
	 *
	 * @param level the level to get the exp at
	 * @return the exp at the specified level
	 */
	public int experienceAt(final int level) {
		if (level < 0 || level > 120) {
			return -1;
		}
		return Constants.SKILLS_XP[level];
	}
}
