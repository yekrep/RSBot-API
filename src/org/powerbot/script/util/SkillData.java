package org.powerbot.script.util;

import org.powerbot.script.methods.Skills;

public final class SkillData {
	public static final int NUM_SKILL = 25;
	public final int[] initialExp = new int[NUM_SKILL];
	public final int[] initialLevels = new int[NUM_SKILL];
	private final Timer timer;

	public SkillData() {
		this(new Timer(0l));
	}

	public SkillData(final Timer timer) {
		for (int index = 0; index < NUM_SKILL; index++) {
			initialExp[index] = Skills.getExperience(index);
			initialLevels[index] = Skills.getRealLevel(index);
		}
		this.timer = timer == null ? new Timer(0l) : timer;
	}

	public int experience(final int index) {
		if (index < 0 || index > NUM_SKILL) {
			throw new IllegalArgumentException("0 > index < " + NUM_SKILL);
		}
		return Skills.getExperience(index) - initialExp[index];
	}

	public int experience(final Rate rate, final int index) {
		return (int) (experience(index) * rate.time / timer.getElapsed());
	}

	public int level(final int index) {
		if (index < 0 || index > NUM_SKILL) {
			throw new IllegalArgumentException("0 > index < " + NUM_SKILL);
		}
		return Skills.getRealLevel(index) - initialLevels[index];
	}

	public int level(final Rate rate, final int index) {
		return (int) (level(index) * rate.time / timer.getElapsed());
	}

	public long timeToLevel(final Rate rate, final int index) {
		final double exp = experience(rate, index);
		if (exp == 0d) {
			return 0l;
		}
		return (long) ((Skills.getExperienceAt(Skills.getRealLevel(index) + 1) - Skills.getExperience(index)) / exp * rate.time);
	}

	public static enum Rate {
		MINUTE(60000d),
		HOUR(3600000d),
		DAY(86400000d),
		WEEK(604800000d);
		public final double time;

		Rate(final double time) {
			this.time = time;
		}

		public double getTime() {
			return time;
		}
	}
}