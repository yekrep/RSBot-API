package org.powerbot.core.script.internal.input;

import java.applet.Applet;
import java.awt.Point;

import org.powerbot.core.script.job.Task;
import org.powerbot.core.script.util.Filter;
import org.powerbot.core.script.wrappers.Targetable;
import org.powerbot.game.client.Client;
import org.powerbot.game.client.input.Mouse;

public class MouseHandler implements Runnable {
	private static final int MAX_STEPS = 20;
	private final MouseImpl mouseImpl;
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
		mouseImpl = null;
		target = null;
	}

	private void clickMouse(final boolean left) {
		final int x = mouse.getX(), y = mouse.getY();
		press(x, y, left);
		Task.sleep(mouseImpl.getPressDuration());
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

	private void moveMouse(final int x, final int y) {
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

			final Point interactPoint = target.targetable.getInteractPoint();
			if (target.interactPoint == null) target.interactPoint = interactPoint;
			Point targetingPoint = target.interactPoint;
			if (interactPoint.x == -1 || interactPoint.y == -1) {
				complete(target);
				return;
			}

			final Point centerPoint = target.targetable.getCenterPoint();
			if (centerPoint.x == -1 || centerPoint.y == -1) {
				complete(target);
				return;
			}

			if (++target.steps > MAX_STEPS) {
				complete(target);
				continue;
			}

			final Point pos = mouse.getLocation();
			final double traverseLength = Math.sqrt(Math.pow(targetingPoint.x - pos.x, 2) + Math.pow(targetingPoint.y - pos.y, 2));
			final double mod = 2.5 + Math.sqrt(Math.pow(interactPoint.x - centerPoint.x, 2) + Math.pow(interactPoint.y - centerPoint.y, 2));
			if (traverseLength < mod) {
				if (target.targetable.contains(pos) && target.filter.accept(pos)) {
					target.callback.execute(this);
					continue;
				}

				final Point nextPoint = target.targetable.getNextPoint();
				if (traverseLength == 0d ||
						Math.pow(targetingPoint.x - nextPoint.x, 2) + Math.pow(targetingPoint.y - nextPoint.y, 2) < Math.pow(mod, 2)) {
					targetingPoint = target.interactPoint = nextPoint;
				}
			}

			if (targetingPoint.x == -1 || targetingPoint.y == -1) continue;
			final Point nextPos = mouseImpl.getNextPoint(targetingPoint);
			moveMouse(nextPos.x, nextPos.y);
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
