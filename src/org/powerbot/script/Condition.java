package org.powerbot.script;

import java.util.concurrent.Callable;

/**
 * An event-driven blocking utility.
 * Frequencies are randomly adjusted by 85-150% to provide a basic antipattern.
 */
public class Condition {
	/**
	 * Blocks until the specified condition is satisfied (returns {@code true}).
	 * This uses a frequency of 600ms for up to 10 tries, i.e. attempting a maximum of 6 seconds.
	 *
	 * @param cond the condition
	 * @return {@code true} if the condition was satisfied, otherwise {@code false}
	 */
	public static boolean wait(final Callable<Boolean> cond) {
		return wait(cond, 600, 10);
	}

	/**
	 * Blocks until the specified condition is satisfied (returns {@code true}).
	 * This uses the specified frequency interval and retries for up to 6 seconds.
	 *
	 * @param cond the condition
	 * @param freq the polling frequency in milliseconds
	 * @return {@code true} if the condition was satisfied, otherwise {@code false}
	 */
	public static boolean wait(final Callable<Boolean> cond, final int freq) {
		return wait(cond, freq, Math.max(2, 6000 / freq));
	}

	/**
	 * Blocks until the specified condition is satisfied (returns {@code true}).
	 *
	 * @param cond  the condition
	 * @param freq  the polling frequency in milliseconds
	 * @param tries the maximum number of attempts before this method returns {@code false}
	 * @return if the condition was satisfied, otherwise {@code false}
	 */
	public static boolean wait(final Callable<Boolean> cond, final int freq, int tries) {
		tries = Math.max(1, tries + Random.nextInt(-1, 2));

		for (int i = 0; i < tries; i++) {
			try {
				final double f = freq * Random.nextDouble(0.85d, 1.5d);
				Thread.sleep(Math.max(5, (int) f));
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

	/**
	 * Sleeps the current thread.
	 *
	 * @param ms the length of time to sleep in milliseconds, which is adjusted by an 85-150% random variance
	 * @return the actual amount of time slept in milliseconds, which is subject to system clock accuracy
	 */
	public static int sleep(final int ms) {
		if (ms <= 0) {
			Thread.yield();
			return 0;
		}
		final long s = System.nanoTime();
		try {
			Thread.sleep((long) (ms * Random.nextDouble(0.85d, 1.5d)));
		} catch (final InterruptedException ignored) {
		}
		return (int) ((System.nanoTime() - s) / 1000000L);
	}

	/**
	 * Sleeps the current thread for a duration that is 10 times the value of {@link Random#getDelay()}.
	 */
	public static void sleep() {
		sleep(Random.getDelay() * 10);
	}
}
