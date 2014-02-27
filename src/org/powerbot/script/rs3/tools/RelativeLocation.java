package org.powerbot.script.rs3.tools;

public class RelativeLocation {
	public static final RelativeLocation NIL = new RelativeLocation(-1f, -1f);
	private final float x;
	private final float y;

	RelativeLocation(final float x, final float y) {
		this.x = x;
		this.y = y;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}
}
