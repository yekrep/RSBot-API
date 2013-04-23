package org.powerbot.golem;

import java.util.Random;

/**
 * @author Paris
 */
public class HardwareSimulator {
	private final static double[] pd;
	private final static Random r;

	static {
		r = new Random(System.nanoTime());
		pd = new double[2];

		final double[] e = {3d, 45d + r.nextInt(11), 12d + r.nextGaussian()};
		final double x[] = {Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().maxMemory() >> 30};

		pd[0] = 4 * Math.log(Math.sin(((Math.PI / x[0]) * Math.PI + 1) / 4)) / Math.PI + 2 * Math.PI * (Math.PI / x[0]) / 3 - 4 * Math.log(Math.sin(.25d)) / Math.PI;
		pd[0] = e[0] * Math.exp(Math.pow(pd[0], 0.75d)) + e[1];
		pd[1] = e[2] * Math.exp(1 / Math.cosh(x[1]));
	}

	public static Random getRandomGenerator() {
		return r;
	}

	public static int getDelayFactor() {
		return (int) ((-1 + 2 * r.nextDouble()) * pd[1] + pd[0]);
	}
}
