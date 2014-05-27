package org.powerbot.script;

import java.applet.Applet;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.powerbot.bot.MouseSpline;

public abstract class Input {
	private final AtomicReference<Component> target;
	protected final AtomicBoolean blocking;
	private final MouseSpline spline;

	protected Input(final AtomicReference<Component> target) {
		this.target = target;
		blocking = new AtomicBoolean(false);
		spline = new MouseSpline();
	}

	public Component getComponent() {
		return ((Applet) target.get()).getComponents()[0];
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

	public boolean click(final int button) {
		press(button);
		try {
			Thread.sleep(spline.getPressDuration());
		} catch (final InterruptedException ignored) {
		}
		//TODO: Maybe move mouse accidentially.
		//TODO: return false -- or re-click?  probably the latter.
		release(button);
		try {
			Thread.sleep(spline.getPressDuration());
		} catch (final InterruptedException ignored) {
		}
		return true;
	}

	public boolean drag(final Point p, final boolean left) {
		return drag(p, left ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3);
	}

	public boolean drag(final Point p, final int button) {
		press(button);
		final boolean b = move(p);
		release(button);
		return b;
	}

	public boolean hop(final Point p) {
		return hop(p.x, p.y);
	}

	public boolean hop(final int x, final int y) {
		return move(x, y);
	}

	public boolean move(final int x, final int y) {
		return move(new Point(x, y));
	}

	public boolean move(final Point p) {
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

	public boolean apply(final Targetable targetable, final Filter<Point> filter) {
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
					try {
						Thread.sleep(TimeUnit.NANOSECONDS.toMillis(d));
					} catch (final InterruptedException ignored) {
					}
				}
			}

			final Point p2 = getLocation(), ep = end.toPoint2D();
			if (p2.equals(ep) && filter.accept(ep)) {
				return true;
			}
		}
		return false;
	}

	public boolean scroll() {
		return scroll(true);
	}

	public abstract boolean scroll(final boolean down);
}
