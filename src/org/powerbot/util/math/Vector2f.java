package org.powerbot.util.math;

import java.awt.Point;
import java.awt.geom.Point2D;

/**
 * @author Paris
 */
public class Vector2f {
	public float x, y;

	public Vector2f() {
		x = 0;
		y = 0;
	}

	public Vector2f(final Vector2f v) {
		x = v.x;
		y = v.y;
	}

	public Vector2f(final float[] v) {
		this(v[0], v[1]);
	}

	public Vector2f(final float x, final float y) {
		this.x = x;
		this.y = y;
	}

	public Vector2f(final Point p) {
		this(p.x, p.y);
	}

	public Vector2f add(final Vector2f u) {
		return new Vector2f(x + u.x, y + u.y);
	}

	public Vector2f mul(final double u) {
		return new Vector2f((float) (x * u), (float) (y * u));
	}

	public Vector2f mul(final Vector2f U) {
		return new Vector2f(x * U.x, y * U.y);
	}

	public final double get2DDistanceTo(final Vector2f v) {
		return Math.sqrt(Math.pow(v.x - x, 2) + Math.pow(v.y - y, 2));
	}

	public final double get2DGradientTo(final Vector2f v) {
		return (double) (v.y - y) / (v.x - x);
	}

	public final double get2DAngleTo(final Vector2f v) {
		double a = Math.atan2(v.y - y, v.x - x);

		if (a < 0) {
			a = Math.abs(a);
		} else {
			a = 2 * Math.PI - a;
		}

		return a;
	}

	public float[] toMatrix() {
		return new float[]{x, y};
	}

	public Point2D to2DPoint() {
		return new Point2D.Float(x, y);
	}

	public final String to2DString() {
		return String.format("(%s, %s)", x, y);
	}

	@Override
	public String toString() {
		return to2DString();
	}
}
