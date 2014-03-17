package org.powerbot.script.rt6;

public class RelativeLocation {
	public static final RelativeLocation NIL = new RelativeLocation(-1f, -1f);
	private final float x;
	private final float z;

	RelativeLocation(final float x, final float z) {
		this.x = x;
		this.z = z;
	}

	public float x() {
		return x;
	}

	public float z() {
		return z;
	}
}
