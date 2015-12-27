package org.powerbot.script.rt4;

import org.powerbot.bot.rt4.client.Client;

public class Skills extends ClientAccessor {
	@Deprecated
	public static final int ATTACK = Constants.SKILLS_ATTACK;
	@Deprecated
	public static final int DEFENSE = Constants.SKILLS_DEFENSE;
	@Deprecated
	public static final int STRENGTH = Constants.SKILLS_STRENGTH;
	@Deprecated
	public static final int HITPOINTS = Constants.SKILLS_HITPOINTS;
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

	public Skills(final ClientContext ctx) {
		super(ctx);
	}

	/**
	 * Returns the real level of the skill at the provided index.
	 *
	 * @param index the index of the skill
	 * @return the current level at the specified index
	 */
	public int realLevel(final int index) {
		final int[] levels = realLevels();
		if (index >= 0 && index < levels.length) {
			return levels[index];
		}
		return -1;
	}

	/**
	 * Returns the effective level of the skill at the provided index.
	 *
	 * @param index the index of the skill
	 * @return the real level at the specified index
	 */
	public int level(final int index) {
		final int[] levels = levels();
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

	public int[] realLevels() {
		final Client c = ctx.client();
		final int[] arr = c != null ? c.getSkillLevels2() : new int[0];
		return arr != null ? arr : new int[0];
	}

	public int[] levels() {
		final Client c = ctx.client();
		final int[] arr = c != null ? c.getSkillLevels1() : new int[0];
		return arr != null ? arr : new int[0];
	}

	public int[] experiences() {
		final Client c = ctx.client();
		final int[] arr = c != null ? c.getSkillExps() : new int[0];
		return arr != null ? arr : new int[0];
	}
}
