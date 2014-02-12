package org.powerbot.os.api.methods;

import java.awt.Point;
import java.util.concurrent.TimeUnit;

import org.powerbot.os.api.internal.HeteroMouse;
import org.powerbot.os.api.internal.MouseSimulator;
import org.powerbot.os.api.util.Filter;
import org.powerbot.os.api.wrappers.Targetable;
import org.powerbot.os.util.math.Vector3;

public class Mouse extends ClientAccessor {
	private final MouseSimulator simulator;

	public Mouse(final ClientContext ctx) {
		super(ctx);
		simulator = new HeteroMouse();
	}

	public Point getLocation() {
		return new Point(-1, -1);//TODO this
	}

	public void hop(final int x, final int y) {
		//TODO: this
	}

	public boolean apply(final Targetable targetable, final Filter<Point> filter) {
		final int STANDARD_ATTEMPTS = 3;
		for (int i = 0; i < STANDARD_ATTEMPTS; i++) {
			final Point mp = getLocation();
			final Vector3 start = new Vector3(mp.x, mp.y, 255);
			final Point p = targetable.getNextPoint();
			if (p.x == -1 || p.y == -1) {
				continue;
			}
			final Vector3 end = new Vector3(p.x, p.y, 0);
			final Iterable<Vector3> spline = simulator.getPath(start, end);
			for (final Vector3 v : spline) {
				hop(v.x, v.y);

				final long m = System.nanoTime();
				if (!targetable.contains(new Point(end.x, end.y))) {
					break;
				}
				final long d = Math.max(0, simulator.getAbsoluteDelay(v.z) - Math.abs(System.nanoTime() - m));
				if (d > 0) {
					try {
						Thread.sleep(TimeUnit.NANOSECONDS.toMillis(d));
					} catch (final InterruptedException ignored) {
					}
				}
			}

			final Point p2 = getLocation(), ep = end.to2DPoint();
			if (p2.equals(ep) && filter.accept(ep)) {
				return true;
			}
		}
		return false;
	}
}
