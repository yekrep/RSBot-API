package org.powerbot.script.util;

import java.util.concurrent.Callable;

/**
 * An event-driven blocking utility.
 *
 * @author Paris
 */
public class Condition {

	/**
	 * Blocks until the specified condition is satisfied (returns {@code true}).
	 * <p/>
	 * Defaults to 10 game ticks (6 seconds blocking).
	 *
	 * @param cond the condition
	 * @return {@code true} if the condition was satisfied, otherwise {@code false}
	 */
	public static boolean wait(final Callable<Boolean> cond) {
		return wait(cond, 600, 10);
	}

	/**
	 * Blocks until the specified condition is satisfied (returns {@code true}).
	 *
	 * @param cond  the condition
	 * @param freq  the polling frequency in milliseconds
	 * @param tries the maximum number of attempts before this method returns {@code false}
	 * @return if the condition was satisfied, otherwise {@code false}
	 */
	public static boolean wait(final Callable<Boolean> cond, final int freq, final int tries) {
		for (int i = 0; i < tries; i++) {
			try {
				Thread.sleep(freq);
			} catch (final InterruptedException ignored) {
				return false;
			}

			final boolean r;
			try {
				r = cond.call();
			} catch (final Exception ignored) {
				return false;
			}
			if (r) {
				return true;
			}
		}

		return false;
	}
}
