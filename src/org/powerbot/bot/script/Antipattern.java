package org.powerbot.bot.script;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.powerbot.script.rs3.tools.ClientAccessor;
import org.powerbot.script.rs3.tools.ClientContext;
import org.powerbot.script.rs3.tools.Validatable;

/**
 * A humanised layer which disrupts mathematically recognisable patterns of gameplay actions.
 *
 */
public abstract class Antipattern extends ClientAccessor implements Runnable, Validatable {
	/**
	 * The frequency at which {@link #isTick()} will return {@code true} expressed as a percentage of {@code value % 100}.
	 * By default this value is 20 (20%).
	 */
	protected final AtomicInteger freq;
	private final Random r;

	public Antipattern(final ClientContext factory) {
		super(factory);
		freq = new AtomicInteger(5);
		final SecureRandom sr = new SecureRandom();
		sr.setSeed(sr.generateSeed(20));
		r = sr;
	}

	/**
	 * Determines whether the antipattern should return to the previous state.
	 *
	 * @return {@code true} if the antipattern should return to the previous state, otherwise {@code false}
	 */
	protected boolean isStateful() {
		return false;
	}

	/**
	 * Determines whether the antipattern should perform aggressive disruption.
	 *
	 * @return {@code true} if the antipattern should perform aggressive disruption, otherwise {@code false}
	 */
	protected boolean isAggressive() {
		return getRandom() % 5 == 0;
	}

	/**
	 * Determines whether the antipattern should run.
	 *
	 * @return {@code true} if the antipattern should run, otherwise {@code false}
	 */
	public final boolean isTick() {
		final int f = freq.get() % 100;
		return f > 99 || (f >= 1 && getRandom() % (100 / f) == 0);
	}

	/**
	 * Returns a non-deterministic random number.
	 *
	 * @return a random number
	 */
	protected long getRandom() {
		return r.nextLong();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isValid() {
		return true;
	}
}
