package org.powerbot.script.rt6;

import org.powerbot.bot.rt6.client.Client;
import org.powerbot.bot.rt6.client.PlayerFacade;
import org.powerbot.bot.rt6.client.Skill;

/**
 * Skills
 */
public class Skills extends ClientAccessor {
	public Skills(final ClientContext context) {
		super(context);
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
		final PlayerFacade info = client.getPlayerFacade();
		final Skill[] skills;
		if (info == null || (skills = info.getSkills()) == null) {
			return new int[0];
		}
		final int[] levels = new int[skills.length];
		for (int i = 0; i < skills.length; i++) {
			final Skill s = skills[i];
			if (!s.isNull()) {
				levels[i] = s.getEffectiveLevel();
			}
		}
		return levels;
	}

	public int[] realLevels() {
		final int[] exps = experiences();
		final int[] levels = new int[exps.length];
		for (int i = 0; i < exps.length; i++) {
			levels[i] = levelAt(exps[i]);
		}
		return levels;
	}

	public int[] experiences() {
		final Client client = ctx.client();
		if (client == null) {
			return new int[0];
		}
		final PlayerFacade info = client.getPlayerFacade();
		final Skill[] skills;
		if (info == null || (skills = info.getSkills()) == null) {
			return new int[0];
		}
		final int[] levels = new int[skills.length];
		for (int i = 0; i < skills.length; i++) {
			final Skill s = skills[i];
			if (!s.isNull()) {
				levels[i] = s.getExperience();
			}
		}
		return levels;
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
