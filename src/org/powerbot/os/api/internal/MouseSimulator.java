package org.powerbot.os.api.internal;

import org.powerbot.os.util.math.Vector3;

/**
 * @author Paris
 */
public interface MouseSimulator {
	/**
	 * Calculates a press duration i.e. delay between click hold and release events.
	 *
	 * @return the suggested duration in milliseconds
	 */
	public int getPressDuration();

	/**
	 * Computes a path between two points.
	 *
	 * @param a the current location
	 * @param b the destination
	 * @return a series of waypoints along a path in the form of 3-dimensional column matrices (x, y, z) where z is the relative instantaneous velocity
	 */
	public Iterable<Vector3> getPath(final Vector3 a, final Vector3 b);

	/**
	 * Translates an instantaneous relative velocity to an absolute (real) delay.
	 *
	 * @param z a pre-calculated velocity value
	 * @return the suggested duration in nanoseconds
	 */
	public long getAbsoluteDelay(final int z);
}
