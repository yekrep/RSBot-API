package org.powerbot.script.internal;

import java.applet.Applet;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import org.powerbot.bot.SelectiveEventQueue;
import org.powerbot.client.Client;
import org.powerbot.client.input.Mouse;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.methods.MethodProvider;
import org.powerbot.util.math.HeteroMouse;
import org.powerbot.util.math.Vector3;

public class MouseHandler extends MethodProvider {
	private static final int MAX_ATTEMPTS = 5;
	public final MouseSimulator simulator;

	public MouseHandler(final MethodContext ctx) {
		super(ctx);
		simulator = new HeteroMouse();
	}

	public void click(final int x, final int y, final int button) {
		try {
			Thread.sleep(simulator.getPressDuration());
		} catch (InterruptedException ignored) {
		}
		press(x, y, button);
		try {
			Thread.sleep(simulator.getPressDuration());
		} catch (InterruptedException ignored) {
		}
		release(x, y, button);
		try {
			Thread.sleep(simulator.getPressDuration());
		} catch (InterruptedException ignored) {
		}
	}

	public void scroll(final boolean down) {
		final Client client = ctx.getClient();
		final Mouse mouse;
		if (client == null || (mouse = client.getMouse()) == null) {
			return;
		}
		if (!mouse.isPresent()) {
			return;
		}
		final Component target = getSource();
		final Point location = getLocation();
		mouse.sendEvent(new MouseWheelEvent(target, MouseWheelEvent.MOUSE_WHEEL, System.currentTimeMillis(), 0, location.x, location.y, 0, false, MouseWheelEvent.WHEEL_UNIT_SCROLL, 1, down ? 1 : -1));
	}

	public void press(final int x, final int y, final int button) {
		final Client client = ctx.getClient();
		final Mouse mouse;
		if (client == null || (mouse = client.getMouse()) == null) {
			return;
		}
		if (!mouse.isPresent()) {
			SelectiveEventQueue.getInstance().defocus();
			return;
		}
		if (mouse.isPressed()) {
			return;
		}

		final Component target = getSource();
		mouse.sendEvent(new MouseEvent(target, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 0, x, y, 1, false, button));
		SelectiveEventQueue.getInstance().focus();
	}

	public void release(final int x, final int y, final int button) {
		final Client client = ctx.getClient();
		final Mouse mouse;
		if (client == null || (mouse = client.getMouse()) == null) {
			return;
		}
		if (!mouse.isPressed()) {
			return;
		}
		if (!mouse.isPresent()) {
			SelectiveEventQueue.getInstance().defocus();
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
		final Client client = ctx.getClient();
		final Mouse mouse;
		if (client == null || (mouse = client.getMouse()) == null) {
			return;
		}
		final long mark = System.currentTimeMillis();
		final Component target = getSource();
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

	public synchronized void handle(final MouseTarget target) {
		final Client client = ctx.getClient();
		final Mouse mouse;
		if (target == null || client == null || (mouse = client.getMouse()) == null) {
			return;
		}

		for (; ; ) {
			if (++target.steps > MAX_ATTEMPTS) {
				target.failed = true;
				break;
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
				break;
			}
			final Vector3 curr = target.curr;
			final Vector3 dest = target.dest;

			final Iterable<Vector3> spline = simulator.getPath(curr, dest);
			for (final Vector3 v : spline) {
				move(v.x, v.y);
				curr.x = v.x;
				curr.y = v.y;
				curr.z = v.z;

				final long m = simulator.getAbsoluteDelay(v.z);
				if (m > 0) {
					try {
						Thread.sleep(m);
					} catch (final InterruptedException ignored) {
					}
				}
			}

			final Point pos = curr.to2DPoint();
			if (target.targetable.contains(pos) && target.filter.accept(pos)) {
				if (target.execute(this)) {
					break;
				}
			}

			final Point next = target.targetable.getNextPoint();
			target.dest = new Vector3(next.x, next.y, 0);
		}
	}

	public Component getSource() {
		final Applet applet = ctx.getBot().applet;
		return applet != null && applet.getComponentCount() > 0 ? applet.getComponent(0) : null;
	}

	public Point getLocation() {
		final Client client = ctx.getClient();
		final Mouse mouse;
		if (client == null || (mouse = client.getMouse()) == null) {
			return new Point(-1, -1);
		}
		return mouse.getLocation();
	}
}
