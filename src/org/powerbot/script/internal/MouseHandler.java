package org.powerbot.script.internal;

import org.powerbot.client.Client;
import org.powerbot.client.input.Mouse;
import org.powerbot.script.lang.Stoppable;
import org.powerbot.script.util.Delay;
import org.powerbot.util.math.HeteroMouse;
import org.powerbot.util.math.Vector3;

import java.applet.Applet;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.concurrent.TimeUnit;

public class MouseHandler implements Runnable, Stoppable {
	private static final int MAX_STEPS = 20;
	private final MouseSimulator simulator;
	private final Object LOCK = new Object();
	private final Applet applet;
	private final Client client;
	private boolean running, stopping = false;
	private MouseTarget target;

	public MouseHandler(final Applet applet, final Client client) {
		this.applet = applet;
		this.client = client;
		simulator = new HeteroMouse();
		target = null;
	}

	public void click(final int button) {
		final Mouse mouse;
		if ((mouse = client.getMouse()) == null) {
			return;
		}
		final int x = mouse.getX(), y = mouse.getY();
		press(x, y, button);
		Delay.sleep(simulator.getPressDuration());
		release(x, y, button);
	}

	public void scroll(boolean down) {
		final Mouse mouse;
		if ((mouse = client.getMouse()) == null) {
			return;
		}
		if (!mouse.isPresent()) {
			return;
		}
		final Component target = getSource();
		Point location = getLocation();
		mouse.sendEvent(new MouseWheelEvent(target, MouseWheelEvent.MOUSE_WHEEL, System.currentTimeMillis(), 0, location.x, location.y, 0, false, MouseWheelEvent.WHEEL_UNIT_SCROLL, 3, down ? 3 : -3));
	}

	public void press(final int x, final int y, final int button) {
		final Mouse mouse;
		if ((mouse = client.getMouse()) == null) {
			return;
		}
		if (!mouse.isPresent() || mouse.isPressed()) {
			return;
		}
		final Component target = getSource();
		mouse.sendEvent(new MouseEvent(target, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 0, x, y, 1, false, button));
	}

	public void release(final int x, final int y, final int button) {
		final Mouse mouse;
		if ((mouse = client.getMouse()) == null) {
			return;
		}
		if (!mouse.isPressed()) {
			return;
		}
		final long mark = System.currentTimeMillis();
		final Component target = getSource();
		mouse.sendEvent(new MouseEvent(target, MouseEvent.MOUSE_RELEASED, mark, 0, x, y, 1, false, button));
		if (mouse.getPressX() == mouse.getX() && mouse.getPressY() == mouse.getY()) {
			mouse.sendEvent(new MouseEvent(target, MouseEvent.MOUSE_CLICKED, mark, 0, x, y, 1, false, button));
		}
	}

	public void move(final int x, final int y) {
		final long mark = System.currentTimeMillis();
		final Component target = getSource();
		final Mouse mouse;
		if ((mouse = client.getMouse()) == null) {
			return;
		}
		final boolean present = x >= 0 && y >= 0 && x < target.getWidth() && y < target.getHeight();
		if (!mouse.isPresent() && present) {
			mouse.sendEvent(new MouseEvent(target, MouseEvent.MOUSE_ENTERED, mark, 0, x, y, 0, false));
		}
		if (mouse.isPressed()) {
			mouse.sendEvent(new MouseEvent(target, MouseEvent.MOUSE_DRAGGED, mark, 0, x, y, 0, false));
		} else if (present) {
			mouse.sendEvent(new MouseEvent(target, MouseEvent.MOUSE_MOVED, mark, 0, x, y, 0, false));
		} else if (mouse.isPresent()) {
			mouse.sendEvent(new MouseEvent(target, MouseEvent.MOUSE_EXITED, mark, 0, x, y, 0, false));
		}
	}

	@Override
	public void run() {
		running = true;

		start:
		while (running) {
			synchronized (LOCK) {
				if (target == null) {
					try {
						LOCK.wait();
					} catch (final InterruptedException ignored) {
					}
				}
			}
			if (target == null) {
				continue;
			}
			final Mouse mouse;
			if ((mouse = client.getMouse()) == null) {
				Delay.sleep(250);
				continue;
			}
			if (++target.steps > MAX_STEPS) {
				target.failed = true;
				complete(target);
				continue;
			}
			final Point loc = mouse.getLocation();
			if (target.curr == null) {
				target.curr = new Vector3(loc.x, loc.y, 255);
			}
			target.curr.x = loc.x;
			target.curr.y = loc.y;
			if (target.dest == null) {
				final Point p = target.targetable.getInteractPoint();
				target.dest = new Vector3(p.x, p.y, 0);
			}
			if (target.dest.x == -1 || target.dest.y == -1) {
				target.failed = true;
				complete(target);
				continue;
			}
			final Vector3 curr = target.curr;
			final Vector3 dest = target.dest;

			final Point centroid = target.targetable.getCenterPoint();
			long m;
			final Iterable<Vector3> spline = simulator.getPath(curr, dest);
			for (final Vector3 v : spline) {
				move(v.x, v.y);
				curr.x = v.x;
				curr.y = v.y;
				curr.z = v.z;

				m = System.currentTimeMillis();
				final double traverseLength = Math.sqrt(Math.pow(dest.x - curr.x, 2) + Math.pow(dest.y - curr.y, 2));
				final double mod = 2.5 + Math.sqrt(Math.pow(dest.x - centroid.x, 2) + Math.pow(dest.y - centroid.y, 2));
				if (traverseLength < mod) {
					final Point pos = curr.to2DPoint();
					if (target.targetable.contains(pos) && target.filter.accept(pos)) {
						target.execute(this);
						continue start;
					}
				}
				m = System.currentTimeMillis() - m;

				final long l = TimeUnit.NANOSECONDS.toMillis(simulator.getAbsoluteDelay(v.z)) - m;
				if (l > 0) {
					Delay.sleep(l);
				}
			}

			final Point next = target.targetable.getNextPoint();
			target.dest = new Vector3(next.x, next.y, 0);
		}
	}

	public void handle(final MouseTarget target) {
		synchronized (LOCK) {
			boolean notify = false;
			if (this.target == null) {
				notify = true;
			}
			this.target = target;
			if (notify) {
				LOCK.notify();
			}

			try {
				LOCK.wait();
			} catch (final InterruptedException ignored) {
			}
		}
	}

	public void complete(final MouseTarget target) {
		synchronized (LOCK) {
			if (target.equals(this.target)) {
				this.target = null;
				LOCK.notifyAll();
			}
		}
	}

	@Override
	public boolean isStopping() {
		return stopping;
	}

	@Override
	public void stop() {
		stopping = true;
		running = false;
	}

	public Component getSource() {
		return applet.getComponentCount() > 0 ? applet.getComponent(0) : null;
	}

	public Mouse getMouse() {
		return client.getMouse();
	}

	public Point getLocation() {
		final Mouse mouse;
		if ((mouse = client.getMouse()) == null) {
			return new Point(-1, -1);
		}
		return mouse.getLocation();
	}
}
