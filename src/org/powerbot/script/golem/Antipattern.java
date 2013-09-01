package org.powerbot.script.golem;

import java.util.concurrent.atomic.AtomicInteger;

import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.methods.MethodProvider;
import org.powerbot.script.wrappers.Validatable;

/**
 * A humanised layer which disrupts mathematically recognisable patterns of gameplay actions.
 *
 * @author Paris
 */
public abstract class Antipattern extends MethodProvider implements Runnable, Validatable {
	/**
	 * The frequency at which {@link #isTick()} will return {@code true} expressed as a percentage of {@code value % 100}.
	 * By default this value is 20 (20%).
	 */
	protected final AtomicInteger freq;

	public Antipattern(final MethodContext factory) {
		super(factory);
		freq = new AtomicInteger(20);
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
		return f > 99 ? true : f < 1 ? false : getRandom() % (100 / f) == 0;
	}

	/**
	 * Returns a non-deterministic random number.
	 *
	 * @return a random number
	 */
	protected int getRandom() {
		return (int) System.nanoTime();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isValid() {
		return true;
	}
}
