package org.powerbot.script.util;

import org.powerbot.script.methods.World;
import org.powerbot.script.methods.WorldImpl;

public final class SkillData extends WorldImpl {
	public static final int NUM_SKILL = 25;
	public final int[] initialExp = new int[NUM_SKILL];
	public final int[] initialLevels = new int[NUM_SKILL];
	private final Timer timer;

	public SkillData(World world) {
		this(world, new Timer(0l));
	}

	public SkillData(World world, final Timer timer) {
		super(world);
		for (int index = 0; index < NUM_SKILL; index++) {
			initialExp[index] = world.skills.getExperience(index);
			initialLevels[index] = world.skills.getRealLevel(index);
		}
		this.timer = timer == null ? new Timer(0l) : timer;
	}

	public int experience(final int index) {
		if (index < 0 || index > NUM_SKILL) {
			throw new IllegalArgumentException("0 > index < " + NUM_SKILL);
		}
		return world.skills.getExperience(index) - initialExp[index];
	}

	public int experience(final Rate rate, final int index) {
		return (int) (experience(index) * rate.time / timer.getElapsed());
	}

	public int level(final int index) {
		if (index < 0 || index > NUM_SKILL) {
			throw new IllegalArgumentException("0 > index < " + NUM_SKILL);
		}
		return world.skills.getRealLevel(index) - initialLevels[index];
	}

	public int level(final Rate rate, final int index) {
		return (int) (level(index) * rate.time / timer.getElapsed());
	}

	public long timeToLevel(final Rate rate, final int index) {
		final double exp = experience(rate, index);
		if (exp == 0d) {
			return 0l;
		}
		return (long) ((world.skills.getExperienceAt(world.skills.getRealLevel(index) + 1) - world.skills.getExperience(index)) / exp * rate.time);
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