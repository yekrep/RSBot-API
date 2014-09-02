package org.powerbot.bot;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import org.powerbot.script.Vector3;

/**
 * A human simulated mouse implementation.
 *
 */
public final class MouseSpline {
	private final Random r;

	public MouseSpline() {
		r = new Random();
		r.setSeed(r.nextLong());
	}

	public int getPressDuration() {
		return org.powerbot.script.Random.getDelay();
	}

	public Iterable<Vector3> getPath(final Vector3 a, final Vector3 b) {
		return getParabola(a, b);
	}

	public long getAbsoluteDelay(final int z) {
		return (0xff - (z % 0xff)) << 16;
	}

	private Queue<Vector3> getParabola(final Vector3 a, final Vector3 b) {
		final Queue<Vector3> l0 = new ArrayDeque<Vector3>();

		final double d = a.distanceTo2D(b);
		final int g0 = (int) d >> 2, g1 = g0 * 2 + 1;

		if (d < 0xff) {
			l0.addAll(impulse(a, b, 4));
			for (final Vector3 v : l0) {
				v.z = 0;
			}
			return l0;
		}

		final int o = r.nextInt(2) + 2;
		final Vector3[] p = new Vector3[o + 2], q = new Vector3[2];
		p[0] = a;
		p[p.length - 1] = b;
		for (int i = 1; i < p.length - 1; i++) {
			p[i] = p[i % 2 == 0 ? p.length - 1 : 0].mul((r.nextDouble() - .5d) / (4 - o) + 1.0d);
			p[i].x += -g0 + r.nextInt(g1);
			p[i].y += -g0 + r.nextInt(g1);
		}

		q[0] = bezier(0, p);
		final int z = (int) Math.log(d) << 2;

		for (int i = 1; i <= z; i++) {
			final float t = 1.0f / z * (float) i;
			q[1] = bezier(t, p);
			q[0].z = Math.abs(q[0].z % 0xff);
			q[1].z = Math.abs(q[1].z % 0xff);
			l0.addAll(impulse(q[0], q[1]));
			q[0] = q[1];
		}

		return l0;
	}

	private Collection<Vector3> impulse(final Vector3 a, final Vector3 b) {
		final double g = a.gradientTo2D(b), d = a.distanceTo2D(b);
		int c = (int) d >> 3;
		if (isBetween(g, -5, -1) || isBetween(g, 1, 5)) {
			c = 0;
		}
		return impulse(a, b, c);
	}

	private static boolean isBetween(final double n, final double min, final double max) {
		return n >= min && n <= max;
	}

	private Collection<Vector3> impulse(final Vector3 a, final Vector3 b, final int c) {
		final List<Vector3> l = new ArrayList<Vector3>();

		final double r = a.angleTo2D(b), m = Math.PI * 1.5d;
		final boolean h = isBetween(r, 0, Math.PI / 4) || isBetween(r, 3 * Math.PI / 4, Math.PI) || isBetween(r, Math.PI, 5 * Math.PI / 4) || isBetween(r, 7 * Math.PI / 4, 2 * Math.PI);
		final double dx = b.x - a.x, dy = b.y - a.y;

		l.add(a);

		for (int i = 1; i <= c; i++) {
			final float t = 1.0f / c * (float) i;
			final double dxt = dx * t, dyt = dy * t;
			double x = a.x + dxt;
			double y = a.y + dyt;
			double z = a.z;
			final double f;
			if (h) {
				f = Math.sin(2 * Math.PI * t) * dxt * (-1 + 2 * this.r.nextDouble()) / m;
				y += f;
			} else {
				f = Math.cos(2 * Math.PI * t) * dyt * (-1 + 2 * this.r.nextDouble()) / m;
				x += f;
			}
			z = Math.ceil(z + Math.pow(Math.abs(f), 3)) % 0xff;
			l.add(new Vector3((int) x, (int) y, (int) z));
		}

		l.add(b);

		return l;
	}

	private static Vector3 bezier(final double t, final Vector3... p) {
		final double u = 1 - t;
		final int n = p.length - 1;
		Vector3 q = p[0].mul(Math.pow(u, n));
		for (int i = 1; i < n; i++) {
			q = q.add(p[i].mul(3 * Math.pow(u, n - i) * Math.pow(t, i)));
		}
		q = q.add(p[n].mul(Math.pow(t, n)));
		return q;
	}
}
