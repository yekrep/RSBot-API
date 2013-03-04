package org.powerbot.golem;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import org.powerbot.core.script.internal.input.MouseSimulator;
import org.powerbot.math.Vector3;

/**
 * A human simulated mouse implementation.
 *
 * @author Paris
 */
public final class HeteroMouse implements MouseSimulator {
	private final double[] pd;
	private final Random r;
	private final static int SHORT_DISTANCE = 250;

	public HeteroMouse() {
		r = new Random(System.nanoTime());
		pd = new double[2];

		final double[] e = { 3d, 45d + r.nextInt(11), 12d + r.nextGaussian() };
		final double x[] = { Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().maxMemory() >> 30 };

		pd[0] = 4 * Math.log(Math.sin(((Math.PI / x[0]) * Math.PI + 1) / 4)) / Math.PI + 2 * Math.PI * (Math.PI / x[0]) / 3 - 4 * Math.log(Math.sin(.25d)) / Math.PI;
		pd[0] = e[0] * Math.exp(Math.pow(pd[0], 0.75d)) + e[1];
		pd[1] = e[2] * Math.exp(1 / Math.cosh(x[1]));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getPressDuration() {
		return (int) ((-1 + 2 * r.nextDouble()) * pd[1] + pd[0]);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterable<Vector3> getPath(final Vector3 a, final Vector3 b) {
		return getParabola(a, b);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getAbsoluteDelay(final int z) {
		return (0xff - (z % 0xff)) << 16;
	}

	private static Queue<Vector3> getParabola(final Vector3 a, Vector3 b) {
		final Queue<Vector3> l0 = new ArrayDeque<Vector3>();
		final Random r = new Random();

		final double d = a.get2DDistanceTo(b);
		final int g0 = (int) d >> 2, g1 = g0 * 2 + 1;

		System.out.println(d);
		if (d < SHORT_DISTANCE) {
			l0.addAll(impulse(a, b, 3));
			return l0;
		}

		final int o = r.nextInt(2) + 2;
		final Vector3[] p = new Vector3[o + 2], q = new Vector3[2];
		p[0] = a; p[p.length - 1] = b;
		for (int i = 1; i < p.length - 1; i++) {
			p[i] = p[i % 2 == 0 ? p.length - 1 : 0].mul((r.nextDouble() - .5d) / (4 - o) + 1.0d);
			p[i].x += -g0 + r.nextInt(g1);
			p[i].y += -g0 + r.nextInt(g1);
		}

		q[0] = bezier(0, p);
		final int z = (int) Math.log(d) << 2;

		for (int i = 1; i <= z; i++) {
			float t = 1.0f / z * (float) i;
			q[1] = bezier(t, p);
			q[0].z = Math.abs(q[0].z % 0xff);
			q[1].z = Math.abs(q[1].z % 0xff);
			l0.addAll(impulse(q[0], q[1]));
			q[0] = q[1];
		}

		return l0;
	}

	private static Collection<Vector3> impulse(final Vector3 a, final Vector3 b) {
		final double g = a.get2DGradientTo(b), d = a.get2DDistanceTo(b);
		int c = (int) d >> 3;
		if (g <= -1 || g >= 1) {
			c = 0;
		}
		return impulse(a, b, c);
	}

	private static Collection<Vector3> impulse(final Vector3 a, final Vector3 b, final int c) {
		final List<Vector3> l = new ArrayList<Vector3>();

		final double g = a.get2DGradientTo(b);
		final double dx = b.x - a.x, dy = b.y - a.y;

		l.add(a);

		for (int i = 1; i <= c; i++) {
			final float t = 1.0f / c * (float) i;
			final double dxt = dx * t, dyt = dy * t;
			double x = a.x + dxt;
			double y = a.y + dyt;
			double z = a.z;
			if (g < 0) {
				y += Math.sin(2 * Math.PI * t) * dyt / Math.PI;
			} else {
				x += Math.sin(2 * Math.PI * t) * dxt / Math.PI;
			}
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
