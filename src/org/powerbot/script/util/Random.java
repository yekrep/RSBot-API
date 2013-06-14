package org.powerbot.script.util;

import ec.util.MersenneTwister;
import org.powerbot.golem.HardwareSimulator;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Random {
	private static final java.util.Random random;

	static {
		final long seed = HardwareSimulator.getRandomSeed();
		java.util.Random r;
		try {
			r = SecureRandom.getInstance("SHA1PRNG");
			r.setSeed(seed);
		} catch (NoSuchAlgorithmException ignored) {
			r = new MersenneTwister(seed & Integer.MAX_VALUE);
		}
		random = r;
	}

	public static boolean nextBoolean() {
		return random.nextBoolean();
	}

	public static int nextInt(final int min, final int max) {
		if (max < min) {
			return max + random.nextInt(min - max);
		}
		return min + (max == min ? 0 : random.nextInt(max - min));
	}

	public static double nextDouble(final double min, final double max) {
		return min + random.nextDouble() * (max - min);
	}

	public static double nextDouble() {
		return random.nextDouble();
	}

	public static int nextGaussian(final int min, final int max, final double sd) {
		return nextGaussian(min, max, min + (max - min) / 2, sd);
	}

	public static int nextGaussian(final int min, final int max, final int mean, final double sd) {
		if (min == max) {
			return min;
		}
		int rand;
		do {
			rand = (int) (random.nextGaussian() * sd + mean);
		} while (rand < min || rand >= max);
		return rand;
	}
}