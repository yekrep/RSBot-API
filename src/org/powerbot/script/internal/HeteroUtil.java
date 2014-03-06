package org.powerbot.script.internal;

import org.powerbot.util.math.HardwareSimulator;

public class HeteroUtil {
	private static final int SCALE_HUMAN_REACTION = 10;

	public static void react() {
		try {
			Thread.sleep(HardwareSimulator.getDelayFactor(SCALE_HUMAN_REACTION));
		} catch (final InterruptedException ignored) {
		}
	}

	public static void hicks(final int depth) {
		final int d = 105 * (int) (Math.log(depth * 2) / Math.log(2));
		try {
			Thread.sleep(d);
		} catch (final InterruptedException ignored) {
		}
	}
}
