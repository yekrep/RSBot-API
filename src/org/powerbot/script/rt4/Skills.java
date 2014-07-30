package org.powerbot.script.rt4;

import org.powerbot.bot.rt4.client.Client;

public class Skills extends ClientAccessor {

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
