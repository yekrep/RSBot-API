package org.powerbot.os.util.math;

public class Vector3 extends Vector2 {
	public int z;

	public Vector3() {
		super();
		z = 0;
	}

	public Vector3(final Vector3 v) {
		this(v.x, v.y, v.z);
	}

	public Vector3(final Vector2 v) {
		this(v, 0);
	}

	public Vector3(final int[] v) {
		super(v[0], v[1]);
		this.z = v[2];
	}

	public Vector3(final Vector2 v, final int z) {
		super(v);
		this.z = z;
	}

	public Vector3(final int x, final int y, final int z) {
		super(x, y);
		this.z = z;
	}

	public Vector3 add(final Vector3 u) {
		return new Vector3(super.add(u), z + u.z);
	}

	public Vector3 mul(final double u) {
		return new Vector3(super.mul(u), (int) (z * u));
	}

	public Vector3 mul(final Vector3 U) {
		return new Vector3(super.mul(U), z * U.z);
	}

	public double get3DDistanceTo(final Vector3 v) {
		return Math.sqrt(Math.pow(v.x - x, 2) + Math.pow(v.y - y, 2) + Math.pow(v.z - z, 2));
	}

	public int[] toMatrix() {
		return new int[]{x, y, z};
	}

	@Override
	public String toString() {
		return String.format("(%s, %s, %s)", x, y, z);
	}
}
