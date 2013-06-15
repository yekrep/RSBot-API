package org.powerbot.script.methods;

import org.powerbot.script.util.Random;

public class ClientLink {
	public ClientFactory ctx;

	public ClientLink(ClientFactory factory) {
		this.ctx = factory;
	}

	/**
	 * Sleeps for the specified duration.
	 *
	 * @param millis the duration in milliseconds.
	 */
	public void sleep(final int millis) {
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
	public void sleep(final int min, final int max) {
		sleep(Random.nextInt(min, max));
	}
}
