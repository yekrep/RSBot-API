package org.powerbot.script.util.math;

public class Vector3f extends Vector2f {
	public final float z;

	public Vector3f() {
		super();
		z = 0;
	}

	public Vector3f(final Vector3f v) {
		this(v.x, v.y, v.z);
	}

	public Vector3f(final Vector2f v) {
		this(v, 0);
	}

	public Vector3f(final float[] v) {
		super(v[0], v[1]);
		this.z = v[2];
	}

	public Vector3f(final Vector2f v, final float z) {
		super(v);
		this.z = z;
	}

	public Vector3f(final float x, final float y, final float z) {
		super(x, y);
		this.z = z;
	}

	public Vector3f add(final Vector3f u) {
		return new Vector3f(super.add(u), z + u.z);
	}

	public Vector3f mul(final double u) {
		return new Vector3f(super.mul(u), (float) (z * u));
	}

	public Vector3f mul(final Vector3f U) {
		return new Vector3f(super.mul(U), z * U.z);
	}

	public double get3DDistanceTo(final Vector3f v) {
		return Math.sqrt(Math.pow(v.x - x, 2) + Math.pow(v.y - y, 2) + Math.pow(v.z - z, 2));
	}

	public float[] toMatrix() {
		return new float[]{x, y, z};
	}

	@Override
	public String toString() {
		return String.format("(%s, %s, %s)", x, y, z);
	}
}
