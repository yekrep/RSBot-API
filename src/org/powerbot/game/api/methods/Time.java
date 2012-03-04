package org.powerbot.game.api.methods;

public class Time {
	public static void sleep(final int toSleep) {
		try {
			long start = System.currentTimeMillis();
			Thread.sleep(toSleep);

			long now;
			while (start + toSleep > (now = System.currentTimeMillis())) {
				Thread.sleep(start + toSleep - now);
			}
		} catch (final InterruptedException ignored) {
		}
	}
}
