package org.powerbot.script.internal.scripts;

import java.util.concurrent.atomic.AtomicInteger;

import org.powerbot.script.PollingScript;
import org.powerbot.script.internal.InternalScript;

/**
 * @author Paris
 */
public class Break extends PollingScript implements InternalScript {
	private final AtomicInteger n1, n2;

	public Break() {
		n2 = new AtomicInteger(0);
		n1 = new AtomicInteger(1);
	}

	@Override
	public int poll() {
		final int n0 = n1.get() + n2.get();
		n2.set(n1.get());
		n1.set(n0);


		final double d = Math.log(Math.pow(n0, 2d) + 1) * 60d;

		if (n0 > 30) {
			log.info("Taking a break for c. " + formatSeconds((int) d));
			sleep((int) (d * 1000 - bias.get() / 2d));
		}

		return n0 * 60 * 1000;
	}

	private static String formatSeconds(final int d) {
		return Integer.toString(d / 60) + ":" + Integer.toString(d % 60);
	}
}
