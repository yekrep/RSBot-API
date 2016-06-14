package org.powerbot.script.rt6;

/**
 * RelativeLocation
 */
public class RelativeLocation {
	public static final RelativeLocation NIL = new RelativeLocation(-1f, -1f, -1);
	private final float x;
	private final int floor;
	private final float z;

	RelativeLocation(final float x, final float z, final int floor) {
		this.x = x;
		this.floor = floor;
		this.z = z;
	}

	public float x() {
		return x;
	}

	public int floor() {
		return floor;
	}

	public float z() {
		return z;
	}
}
