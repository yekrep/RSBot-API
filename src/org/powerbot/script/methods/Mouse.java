package org.powerbot.script.methods;

import java.awt.Point;
import java.awt.event.MouseEvent;

import org.powerbot.bot.*;
import org.powerbot.bot.World;
import org.powerbot.client.Client;
import org.powerbot.script.internal.MouseHandler;
import org.powerbot.script.internal.MouseTarget;
import org.powerbot.script.util.Delay;
import org.powerbot.script.util.Filter;
import org.powerbot.script.wrappers.Targetable;

public class Mouse {
	public static Point getLocation() {
		final Client client = World.getWorld().getClient();
		final org.powerbot.client.input.Mouse mouse;
		if (client == null || (mouse = client.getMouse()) == null) return new Point(-1, -1);
		return mouse.getLocation();
	}

	public static Point getPressLocation() {
		final Client client = World.getWorld().getClient();
		final org.powerbot.client.input.Mouse mouse;
		if (client == null || (mouse = client.getMouse()) == null) return new Point(-1, -1);
		return mouse.getPressLocation();
	}

	public static long getPressTime() {
		final Client client = World.getWorld().getClient();
		final org.powerbot.client.input.Mouse mouse;
		if (client == null || (mouse = client.getMouse()) == null) return -1;
		return mouse.getPressTime();
	}

	public static boolean isPressed() {
		final Client client = World.getWorld().getClient();
		final org.powerbot.client.input.Mouse mouse;
		return !(client == null || (mouse = client.getMouse()) == null) && mouse.isPressed();
	}

	public static boolean isPresent() {
		final Client client = World.getWorld().getClient();
		final org.powerbot.client.input.Mouse mouse;
		return !(client == null || (mouse = client.getMouse()) == null) && mouse.isPresent();
	}

	public static boolean hop(final Point p) {
		return hop(p.x, p.y);
	}

	public static boolean hop(final int x, final int y) {
		final MouseHandler handler = Bot.mouseHandler();
		if (handler == null) return false;

		handler.move(x, y);
		return true;
	}

	public static boolean click(final int x, final int y, final boolean left) {
		return move(x, y) && click(left ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3);
	}

	public static boolean click(final boolean left) {
		return click(left ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3);
	}

	public static boolean click(final int button) {
		final MouseHandler handler = Bot.mouseHandler();
		if (handler == null) return false;

		handler.click(button);
		return true;
	}

	public static boolean click(final Point p, final boolean left) {
		return move(p) && click(left);
	}

	public static boolean click(final Targetable target, final boolean left) {
		final MouseHandler handler = Bot.mouseHandler();
		if (handler == null) return false;

		final MouseTarget t;
		handler.handle(t = new MouseTarget(target, MouseTarget.DUMMY) {
			@Override
			public void execute(final MouseHandler handler) {
				Delay.sleep(0, 350);
				if (filter.accept(handler.getLocation())) {
					handler.click(left ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3);
					handler.complete(this);
				}
			}
		});
		return !t.failed;
	}

	public static boolean drag(final Point p1, final Point p2, final boolean left) {
		return drag(p1, p2, left ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3);
	}

	public static boolean drag(final Point p1, final Point p2, final int button) {
		final MouseHandler handler = Bot.mouseHandler();
		if (handler == null) return false;

		Point loc = handler.getLocation();
		if (!loc.equals(p1)) if (move(p1)) {
			handler.press(p1.x, p1.y, button);
			if (move(p2)) {
				handler.release(p2.x, p2.y, button);
				return true;
			}
		}
		return false;
	}

	public static boolean drag(final int x1, final int y1, final int x2, final int y2, final boolean left) {
		return drag(x1, y1, x2, y2, left ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3);
	}

	public static boolean drag(final int x1, final int y1, final int x2, final int y2, final int button) {
		return drag(new Point(x1, y1), new Point(x2, y2), button);
	}

	public static boolean drag(final int x, final int y, final boolean left) {
		return drag(getLocation(), new Point(x, y), left);
	}

	public static boolean drag(final Point p, final boolean left) {
		return drag(getLocation(), p, left);
	}

	public static boolean drag(final int x, final int y, final int button) {
		return drag(getLocation(), new Point(x, y), button);
	}

	public static boolean drag(final Point p, final int button) {
		return drag(getLocation(), p, button);
	}

	public static boolean move(final Targetable target) {
		return move(target, MouseTarget.DUMMY);
	}

	public static boolean move(final Targetable target, final Filter<Point> filter) {
		final MouseHandler handler = Bot.mouseHandler();
		if (handler == null) return false;

		final MouseTarget t;
		handler.handle(t = new MouseTarget(target, filter) {
			@Override
			public void execute(final MouseHandler handler) {
				handler.complete(this);
			}
		});
		return !t.failed;
	}

	public static boolean move(final int x, final int y) {
		return move(new Point(x, y));
	}

	public static boolean move(final Point p) {
		final MouseHandler handler = Bot.mouseHandler();
		if (handler == null) return false;

		final MouseTarget t;
		final Targetable targetable = new Targetable() {
			@Override
			public Point getInteractPoint() {
				return p;
			}

			@Override
			public Point getNextPoint() {
				return p;
			}

			@Override
			public Point getCenterPoint() {
				return p;
			}

			@Override
			public boolean contains(final Point point) {
				return point.equals(p);
			}
		};

		handler.handle(t = new MouseTarget(targetable, MouseTarget.DUMMY) {
			@Override
			public void execute(final MouseHandler handler) {
				handler.complete(this);
			}
		});
		return !t.failed;
	}
}
