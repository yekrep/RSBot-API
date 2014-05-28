package org.powerbot.script;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.concurrent.atomic.AtomicBoolean;

import org.powerbot.bot.MouseSpline;

public abstract class Input {
	protected final AtomicBoolean blocking;
	private final MouseSpline spline;

	protected Input() {
		blocking = new AtomicBoolean(false);
		spline = new MouseSpline();
	}


	public final boolean blocking() {
		return blocking.get();
	}

	public void blocking(final boolean b) {
		blocking.set(b);
	}

	public abstract void send(final String s);

	public final void sendln(final String s) {
		send(s + "\n");
	}

	public abstract Point getLocation();

	public abstract Point getPressLocation();

	public abstract long getPressWhen();

	public abstract boolean press(final int button);

	public abstract boolean release(final int button);

	public final boolean click(final int x, final int y, final int button) {
		return click(new Point(x, y), button);
	}

	public final boolean click(final int x, final int y, final boolean left) {
		return click(new Point(x, y), left);
	}

	public final boolean click(final Point point, final int button) {
		return move(point) && click(button);
	}

	public final boolean click(final Point point, final boolean left) {
		return move(point) && click(left);
	}

	public final boolean click(final boolean left) {
		return click(left ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3);
	}

	public final boolean click(final int button) {
		press(button);
		//TODO: Maybe move mouse accidentially.
		//TODO: return false -- or re-click?  probably the latter.
		Condition.sleep(spline.getPressDuration());
		release(button);
		Condition.sleep(spline.getPressDuration());
		return true;
	}

	public final boolean drag(final Point p, final boolean left) {
		return drag(p, left ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3);
	}

	public final boolean drag(final Point p, final int button) {
		press(button);
		final boolean b = move(p);
		release(button);
		return b;
	}

	public final boolean hop(final Point p) {
		return hop(p.x, p.y);
	}

	public final boolean hop(final int x, final int y) {
		return move(x, y);
	}

	public final boolean move(final int x, final int y) {
		return move(new Point(x, y));
	}

	public final boolean move(final Point p) {
		return apply(
				new Targetable() {
					@Override
					public Point nextPoint() {
						return p;
					}

					@Override
					public boolean contains(final Point point) {
						return p.equals(point);
					}
				},
				new Filter<Point>() {
					@Override
					public boolean accept(final Point point) {
						return p.equals(point);
					}
				}
		);
	}

	public final boolean apply(final Targetable targetable, final Filter<Point> filter) {
		final Point target_point = new Point(-1, -1);
		final int STANDARD_ATTEMPTS = 3;
		for (int i = 0; i < STANDARD_ATTEMPTS; i++) {
			final Point mp = getLocation();
			final Vector3 start = new Vector3(mp.x, mp.y, 255);
			final Point p = targetable.nextPoint();
			if (p.x == -1 || p.y == -1) {
				continue;
			}
			target_point.move(p.x, p.y);
			final Vector3 end = new Vector3(p.x, p.y, 0);
			final Iterable<Vector3> spline = this.spline.getPath(start, end);
			for (final Vector3 v : spline) {
				hop(v.x, v.y);

				final long m = System.nanoTime();
				if (!targetable.contains(new Point(end.x, end.y))) {
					break;
				}
				final long d = Math.max(0, this.spline.getAbsoluteDelay(v.z) - Math.abs(System.nanoTime() - m));
				if (d > 0) {
					Condition.sleep((int) (d / 1e6));
				}
			}

			final Point p2 = getLocation(), ep = end.toPoint2D();
			if (p2.equals(ep) && filter.accept(ep)) {
				return true;
			}
		}
		return false;
	}

	public final boolean scroll() {
		return scroll(true);
	}

	public abstract boolean scroll(final boolean down);
}
