package org.powerbot.game.api.util;

/**
 * A utility for manipulating time.
 *
 * @author Timer
 */
public class Time {
	/**
	 * @param time The number of milliseconds to ensure sleeping for.
	 */
	public static void sleep(final int time) {
		try {
			final long start = System.currentTimeMillis();
			Thread.sleep(time);
			long now;
			while (start + time > (now = System.currentTimeMillis())) {
				Thread.sleep(start + time - now);
			}
		} catch (final InterruptedException ignored) {
		}
	}
}
