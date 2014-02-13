package org.powerbot.os.api.tools;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

import org.powerbot.os.api.ClientAccessor;
import org.powerbot.os.api.ClientContext;
import org.powerbot.os.api.internal.InputSimulator;
import org.powerbot.os.api.internal.MouseSimulator;
import org.powerbot.os.api.util.Filter;
import org.powerbot.os.bot.SelectiveEventQueue;
import org.powerbot.os.bot.event.EventDispatcher;
import org.powerbot.os.bot.event.PaintListener;
import org.powerbot.os.util.math.Vector3;

public class Mouse extends ClientAccessor {
	private final SelectiveEventQueue queue;
	private final MouseSimulator simulator;

	public Mouse(final ClientContext ctx) {
		super(ctx);
		queue = SelectiveEventQueue.getInstance();
		simulator = new MouseSimulator();
	}

	public Point getLocation() {
		final InputSimulator engine = queue.getEngine();
		return engine != null ? engine.getLocation() : new Point(-1, -1);
	}

	public boolean click(final boolean left) {
		return click(left ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3);
	}

	public boolean click(final int button) {
		final InputSimulator engine = queue.getEngine();
		if (engine == null) {
			return false;
		}
		try {//TODO: do we need this delay...?
			Thread.sleep(simulator.getPressDuration());
		} catch (final InterruptedException ignored) {
		}
		engine.press(button);
		try {
			Thread.sleep(simulator.getPressDuration());
		} catch (final InterruptedException ignored) {
		}
		//TODO: Maybe move mouse accidentially.
		//TODO: return false -- or re-click?  probably the latter.
		engine.release(button);
		try {
			Thread.sleep(simulator.getPressDuration());
		} catch (final InterruptedException ignored) {
		}
		return true;
	}

	public boolean hop(final Point p) {
		return hop(p.x, p.y);
	}

	public boolean hop(final int x, final int y) {
		final InputSimulator engine = queue.getEngine();
		return engine != null && engine.move(x, y);
	}

	public boolean move(final int x, final int y) {
		return move(new Point(x, y));
	}

	public boolean move(final Point p) {
		return apply(
				new Targetable() {
					@Override
					public Point getNextPoint() {
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
		final Color c = getStroke(targetable.getClass());
		final PaintListener l = new PaintListener() {
			@Override
			public void repaint(final Graphics render) {
				if (target_point.x == -1 || target_point.y == -1) {
					return;
				}
				render.setColor(c);
				render.fillOval(target_point.x - 5, target_point.y - 5, 10, 10);
			}
		};
		final EventDispatcher dispatcher = ctx.bot().dispatcher;
		dispatcher.add(l);
		final int STANDARD_ATTEMPTS = 3;
		for (int i = 0; i < STANDARD_ATTEMPTS; i++) {
			final Point mp = getLocation();
			final Vector3 start = new Vector3(mp.x, mp.y, 255);
			final Point p = targetable.getNextPoint();
			if (p.x == -1 || p.y == -1) {
				continue;
			}
			target_point.move(p.x, p.y);
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
				dispatcher.remove(l);
				return true;
			}
		}
		dispatcher.remove(l);
		return false;
	}

	private Color getStroke(final Class<?> clazz) {
		final Field f;
		try {
			f = clazz.getField("TARGET_STROKE_COLOR");
			if (f != null) {
				final Object o = f.get(null);
				if (o instanceof Color) {
					return (Color) o;
				}
			}
		} catch (final NoSuchFieldException ignored) {
		} catch (final IllegalAccessException ignored) {
		}
		return new Color(0, 0, 0, 50);
	}
}
