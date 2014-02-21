package org.powerbot.script.util;

import java.security.SecureRandom;

public class Random {
	private static final java.util.Random random;

	static {
		java.util.Random r;
		try {
			r = SecureRandom.getInstance("SHA1PRNG", "SUN");
		} catch (final Exception ignored) {
			r = new java.util.Random();
		}
		r.setSeed(r.nextLong());
		random = r;
	}

	/**
	 * Generates a random boolean.
	 *
	 * @return returns true or false randomly
	 */
	public static boolean nextBoolean() {
		return random.nextBoolean();
	}

	/**
	 * Returns a pseudo-generated random number.
	 *
	 * @param min minimum bound (inclusive)
	 * @param max maximum bound (exclusive)
	 * @return the random number between min and max (inclusive, exclusive)
	 */
	public static int nextInt(final int min, final int max) {
		if (max < min) {
			return max + random.nextInt(min - max);
		}
		return min + (max == min ? 0 : random.nextInt(max - min));
	}

	/**
	 * Returns the next pseudo-random double, distributed between min and max.
	 *
	 * @param min the minimum bound
	 * @param max the maximum bound
	 * @return the random number between min and max
	 */
	public static double nextDouble(final double min, final double max) {
		if (max < min) {
			return max + random.nextDouble() * (min - max);
		}
		return min + random.nextDouble() * (max - min);
	}

	/**
	 * Returns the next pseudo-random double.
	 *
	 * @return the next pseudo-random, a value between {@code 0.0} and {@code 1.0}.
	 */
	public static double nextDouble() {
		return random.nextDouble();
	}

	/**
	 * Returns the next pseudorandom, Gaussian ("normally") distributed {@code double} value with mean {@code 0.0} and
	 * standard deviation {@code 1.0}.
	 *
	 * @return a gaussian distributed number
	 */
	public static double nextGaussian() {
		return random.nextGaussian();
	}

	/**
	 * Returns a pseudo-random gaussian distributed number between the given min and max with the provided standard deviation.
	 * <p/>
	 * Mean defaults as {@code (max - min) / 2}.
	 *
	 * @param min the minimum bound
	 * @param max the maximum bound
	 * @param sd  the standard deviation from the mean
	 * @return a gaussian distributed number between the provided bounds
	 */
	public static int nextGaussian(final int min, final int max, final double sd) {
		return nextGaussian(min, max, (max - min) / 2, sd);
	}

	/**
	 * Returns a pseudo-random gaussian distributed number between the given min and max with the provided standard deviation.
	 *
	 * @param min  the minimum bound
	 * @param max  the maximum bound
	 * @param mean the mean value
	 * @param sd   the standard deviation from the mean
	 * @return a gaussian distributed number between the provided bounds
	 */
	public static int nextGaussian(final int min, final int max, final int mean, final double sd) {
		return min + Math.abs(((int) (random.nextGaussian() * sd + mean)) % (max - min));
	}
}
