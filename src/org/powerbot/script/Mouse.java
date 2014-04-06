package org.powerbot.script;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.concurrent.TimeUnit;

import org.powerbot.bot.InputSimulator;
import org.powerbot.bot.MouseSpline;
import org.powerbot.bot.SelectiveEventQueue;
import org.powerbot.gui.BotChrome;

public class Mouse<C extends ClientContext> extends ClientAccessor<C> {
	private final SelectiveEventQueue queue;
	private final MouseSpline simulator;

	public Mouse(final C ctx) {
		super(ctx);
		queue = SelectiveEventQueue.getInstance();
		simulator = new MouseSpline();
	}

	public Point getLocation() {
		final InputSimulator engine = queue.getEngine();
		if (engine != null) {
			return engine.getLocation();
		}
		final Component c = queue.getComponent();
		Point p = c != null ? c.getMousePosition() : null;
		if (p == null) {
			final Component overlay = BotChrome.getInstance().overlay.get();
			if (overlay != null) {
				p = overlay.getMousePosition();//TODO: eek r we detected?!
			}
		}
		return p != null ? p : new Point(-1, -1);
	}

	public Point getPressLocation() {
		final InputSimulator engine = queue.getEngine();
		return engine != null ? engine.getPressLocation() : new Point(-1, -1);
	}

	public long getPressWhen() {
		final InputSimulator engine = queue.getEngine();
		return engine != null ? engine.getPressWhen() : -1;
	}

	public boolean press(final int button) {
		final InputSimulator engine = queue.getEngine();
		if (engine == null) {
			return false;
		}
		engine.press(button);
		return true;
	}

	public boolean release(final int button) {
		final InputSimulator engine = queue.getEngine();
		if (engine == null) {
			return false;
		}
		engine.release(button);
		return true;
	}

	public boolean click(final int x, final int y, final int button) {
		return click(new Point(x, y), button);
	}

	public boolean click(final int x, final int y, final boolean left) {
		return click(new Point(x, y), left);
	}

	public boolean click(final Point point, final int button) {
		return move(point) && click(button);
	}

	public boolean click(final Point point, final boolean left) {
		return move(point) && click(left);
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

	public boolean drag(final Point p, final boolean left) {
		return drag(p, left ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3);
	}

	public boolean drag(final Point p, final int button) {
		final InputSimulator engine = queue.getEngine();
		if (engine == null) {
			return false;
		}
		engine.press(button);
		final boolean b = move(p);
		engine.release(button);
		return b;
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

	public boolean scroll(final boolean down) {
		final InputSimulator simulator = queue.getEngine();
		return simulator != null && simulator.scroll(down);
	}
}
