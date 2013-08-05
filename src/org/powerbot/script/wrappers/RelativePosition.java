package org.powerbot.script.wrappers;

public class RelativePosition {
	public static final RelativePosition NIL = new RelativePosition(-1f, -1f);
	private final float x;
	private final float y;

	RelativePosition(float x, float y) {
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
