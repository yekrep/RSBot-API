package org.powerbot.script.util;

import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.methods.MethodProvider;

/**
 * A monitor that tracks skill exp gains by time.
 *
 * @author Timer
 * @deprecated consumers should use their own calculations to determine exp gains and rates
 */
@Deprecated
@SuppressWarnings("deprecation")
public final class SkillData extends MethodProvider {
	public static final int NUM_SKILL = 26;
	public final int[] initialExp = new int[NUM_SKILL];
	public final int[] initialLevels = new int[NUM_SKILL];
	private final Timer timer;

	public SkillData(final MethodContext ctx) {
		this(ctx, new Timer(0l));
	}

	public SkillData(final MethodContext ctx, final Timer timer) {
		super(ctx);
		for (int index = 0; index < NUM_SKILL; index++) {
			initialExp[index] = ctx.skills.getExperience(index);
			initialLevels[index] = ctx.skills.getRealLevel(index);
		}
		this.timer = timer == null ? new Timer(0l) : timer;
	}

	public int experience(final int index) {
		if (index < 0 || index > NUM_SKILL) {
			throw new IllegalArgumentException("0 > index < " + NUM_SKILL);
		}
		return ctx.skills.getExperience(index) - initialExp[index];
	}

	public int experience(final Rate rate, final int index) {
		return (int) (experience(index) * rate.time / timer.getElapsed());
	}

	public int level(final int index) {
		if (index < 0 || index > NUM_SKILL) {
			throw new IllegalArgumentException("0 > index < " + NUM_SKILL);
		}
		return ctx.skills.getRealLevel(index) - initialLevels[index];
	}

	public int level(final Rate rate, final int index) {
		return (int) (level(index) * rate.time / timer.getElapsed());
	}

	public long timeToLevel(final Rate rate, final int index) {
		final double exp = experience(rate, index);
		if (exp == 0d) {
			return 0l;
		}
		return (long) ((ctx.skills.getExperienceAt(ctx.skills.getRealLevel(index) + 1) - ctx.skills.getExperience(index)) / exp * rate.time);
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