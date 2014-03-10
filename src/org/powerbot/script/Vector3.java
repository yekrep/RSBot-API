package org.powerbot.script;

import java.awt.Point;

public class Vector3 {
	public int x, y, z;

	public Vector3() {
		this(0, 0, 0);
	}

	public Vector3(final int x, final int y, final int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3 add(final Vector3 u) {
		return new Vector3(x + u.x, y + u.y, z + u.z);
	}

	public Vector3 dot(final double u) {
		return new Vector3((int) (x * u), (int) (y * u), (int) (z * u));
	}

	public Vector3 dot(final Vector3 u) {
		return new Vector3(x * u.x, y * u.y, z * u.z);
	}

	public double distanceTo2D(final Vector3 v) {
		return Math.sqrt(Math.pow(v.x - x, 2) + Math.pow(v.y - y, 2));
	}

	public double distanceTo(final Vector3 v) {
		return Math.sqrt(Math.pow(v.x - x, 2) + Math.pow(v.y - y, 2) + Math.pow(v.z - z, 2));
	}

	public final double gradientTo2D(final Vector3 v) {
		return (double) (v.y - y) / (v.x - x);
	}

	public final double angleTo2D(final Vector3 v) {
		double a = Math.atan2(v.y - y, v.x - x);

		if (a < 0) {
			a = Math.abs(a);
		} else {
			a = 2 * Math.PI - a;
		}

		return a;
	}

	public int[] toMatrix() {
		return new int[]{x, y, z};
	}

	public Point toPoint2D() {
		return new Point(x, y);
	}

	@Override
	public String toString() {
		return String.format("(%s, %s, %s)", x, y, z);
	}
}
