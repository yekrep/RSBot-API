package org.powerbot.api.internal.util.math;

import java.awt.Point;

public class Vector2 {
	public int x, y;

	public Vector2() {
		x = 0;
		y = 0;
	}

	public Vector2(final Vector2 v) {
		x = v.x;
		y = v.y;
	}

	public Vector2(final int[] v) {
		this(v[0], v[1]);
	}

	public Vector2(final int x, final int y) {
		this.x = x;
		this.y = y;
	}

	public Vector2(final Point p) {
		this(p.x, p.y);
	}

	public Vector2 add(final Vector2 u) {
		return new Vector2(x + u.x, y + u.y);
	}

	public Vector2 mul(final double u) {
		return new Vector2((int) (x * u), (int) (y * u));
	}

	public Vector2 mul(final Vector2 U) {
		return new Vector2(x * U.x, y * U.y);
	}

	public final double get2DDistanceTo(final Vector2 v) {
		return Math.sqrt(Math.pow(v.x - x, 2) + Math.pow(v.y - y, 2));
	}

	public final double get2DGradientTo(final Vector2 v) {
		return (double) (v.y - y) / (v.x - x);
	}

	public final double get2DAngleTo(final Vector2 v) {
		double a = Math.atan2(v.y - y, v.x - x);

		if (a < 0) {
			a = Math.abs(a);
		} else {
			a = 2 * Math.PI - a;
		}

		return a;
	}

	public int[] toMatrix() {
		return new int[]{x, y};
	}

	public Point to2DPoint() {
		return new Point(x, y);
	}

	public final String to2DString() {
		return String.format("(%s, %s)", x, y);
	}

	@Override
	public String toString() {
		return to2DString();
	}
}
