package org.powerbot.script.util;

public class Delay {
	public static void sleep(final long millis) {
		try {
			Thread.sleep(millis);
		} catch (final InterruptedException ignored) {
		}
	}

	public static void sleep(final int min, final int max) {
		sleep(Random.nextInt(min, max));
	}
}
