package org.powerbot.core.script.internal.input;

import java.applet.Applet;
import java.awt.Point;
import java.util.concurrent.TimeUnit;

import org.powerbot.core.script.job.Task;
import org.powerbot.core.script.util.Filter;
import org.powerbot.core.script.wrappers.Targetable;
import org.powerbot.game.client.Client;
import org.powerbot.game.client.input.Mouse;
import org.powerbot.math.Vector3;

public class MouseHandler implements Runnable {
	private static final int MAX_STEPS = 20;
	private final MouseSimulator simulator;
	private final Object LOCK = new Object();
	private final Client client;
	private final Applet applet;
	private final Mouse mouse;
	private boolean running;
	private Target target;

	public MouseHandler(final Client client) {
		this.client = client;
		this.applet = (Applet) client;
		this.mouse = client.getMouse();
		simulator = null;
		target = null;
	}

	private void click(final boolean left) {
		final int x = mouse.getX(), y = mouse.getY();
		press(x, y, left);
		Task.sleep(simulator.getPressDuration());
		release(x, y, left);
	}

	private void press(final int x, final int y, final boolean left) {
		if (!mouse.isPresent() || mouse.isPressed()) return;
		//TODO MOUSE_PRESSED
	}

	private void release(final int x, final int y, final boolean left) {
		if (!mouse.isPresent() || !mouse.isPressed()) return;
		final long mark = System.currentTimeMillis();
		//TODO MOUSE_RELEASED
		//TODO do not fire if dragged
		{
			//TODO MOUSE_CLICKED
		}
		//TODO reset drag length

	}

	private void move(final int x, final int y) {
		//TODO move mouse
	}

	@Override
	public void run() {
		running = true;

		while (running) {
			synchronized (LOCK) {
				if (target == null) {
					try {
						LOCK.wait();
					} catch (final InterruptedException ignored) {
					}
				}
			}
			if (target == null) continue;
			if (++target.steps > MAX_STEPS) {
				complete(target);
				continue;
			}

			if (target.curr == null) {
				final Point p = mouse.getLocation();
				target.curr = new Vector3(p.x, p.y, 255);
			}
			if (target.dest == null) {
				final Point p = target.targetable.getInteractPoint();
				target.dest = new Vector3(p.x, p.y, 0);
			}
			if (target.dest.x == -1 || target.dest.y == -1) {
				complete(target);
				continue;
			}
			final Vector3 curr = target.curr;
			final Vector3 dest = target.dest;

			final Iterable<Vector3> spline = simulator.getPath(curr, dest);
			for (final Vector3 v : spline) {
				move(v.x, v.y);
				Task.sleep(TimeUnit.NANOSECONDS.toMillis(simulator.getAbsoluteDelay(v.z)));
			}

			final Point centroid = target.targetable.getCenterPoint();
			final double traverseLength = Math.sqrt(Math.pow(dest.x - curr.x, 2) + Math.pow(dest.y - curr.y, 2));
			final double mod = 2.5 + Math.sqrt(Math.pow(dest.x - centroid.x, 2) + Math.pow(dest.y - centroid.y, 2));
			if (traverseLength < mod) {
				final Point pos = curr.to2DPoint();
				if (target.targetable.contains(pos) && target.filter.accept(pos)) {
					target.callback.execute(this);
					continue;
				}

				final Point next = target.targetable.getInteractPoint();
				dest.x = next.x;
				dest.y = next.y;
				continue;
			}
		}
	}

	public void handle(final Targetable target, final Filter<Point> filter, final MouseCallback mouseCallback) {
		synchronized (LOCK) {
			boolean notify = false;
			if (this.target == null) notify = true;
			this.target = new Target(target, filter, mouseCallback);
			if (notify) LOCK.notify();

			try {
				LOCK.wait();
			} catch (final InterruptedException ignored) {
			}
		}
	}

	public void complete(final Target target) {
		synchronized (LOCK) {
			if (target.equals(this.target)) {
				this.target = null;
				LOCK.notifyAll();
			}
		}
	}

	public void stop() {
		running = false;
	}
}
