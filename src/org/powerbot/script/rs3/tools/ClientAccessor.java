package org.powerbot.script.rs3.tools;

import org.powerbot.script.util.Random;

public class ClientAccessor {
	public final ClientContext ctx;

	public ClientAccessor(final ClientContext ctx) {
		this.ctx = ctx;
	}

	/**
	 * Sleeps for the specified duration.
	 *
	 * @param millis the duration in milliseconds.
	 */
	@Deprecated
	public void sleep(final int millis) {
		if (millis <= 0) {
			return;
		}

		try {
			Thread.sleep(millis);
		} catch (final InterruptedException ignored) {
		}
	}

	/**
	 * Sleeps for a random duration between the specified intervals.
	 *
	 * @param min the minimum duration (inclusive)
	 * @param max the maximum duration (exclusive)
	 */
	@Deprecated
	public void sleep(final int min, final int max) {
		sleep(Random.nextInt(min, max));
	}
}
