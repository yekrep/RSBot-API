package org.powerbot.script.xenon;

import java.awt.Point;
import java.awt.event.MouseEvent;

import org.powerbot.bot.Bot;
import org.powerbot.game.client.Client;
import org.powerbot.script.internal.input.MouseHandler;
import org.powerbot.script.internal.input.MouseTarget;
import org.powerbot.script.task.Task;
import org.powerbot.script.xenon.util.Filter;
import org.powerbot.script.xenon.wrappers.Targetable;

public class Mouse {
	public static Point getLocation() {
		final Client client = Bot.client();
		final org.powerbot.game.client.input.Mouse mouse;
		if (client == null || (mouse = client.getMouse()) == null) return new Point(-1, -1);
		return mouse.getLocation();
	}

	public static Point getPressLocation() {
		final Client client = Bot.client();
		final org.powerbot.game.client.input.Mouse mouse;
		if (client == null || (mouse = client.getMouse()) == null) return new Point(-1, -1);
		return mouse.getPressLocation();
	}

	public static long getPressTime() {
		final Client client = Bot.client();
		final org.powerbot.game.client.input.Mouse mouse;
		if (client == null || (mouse = client.getMouse()) == null) return -1;
		return mouse.getPressTime();
	}

	public static boolean isPressed() {
		final Client client = Bot.client();
		final org.powerbot.game.client.input.Mouse mouse;
		if (client == null || (mouse = client.getMouse()) == null) return false;
		return mouse.isPressed();
	}

	public static boolean isPresent() {
		final Client client = Bot.client();
		final org.powerbot.game.client.input.Mouse mouse;
		if (client == null || (mouse = client.getMouse()) == null) return false;
		return mouse.isPresent();
	}

	public static void hop(final Point p) {
		hop(p.x, p.y);
	}

	public static void hop(final int x, final int y) {
		final MouseHandler handler = Bot.mouseHandler();
		if (handler == null) return;

		handler.move(x, y);
	}

	public static void click(final boolean left) {
		click(left ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3);
	}

	public static void click(final int button) {
		final MouseHandler handler = Bot.mouseHandler();
		if (handler == null) return;

		handler.click(button);
	}

	public static boolean click(final Targetable target, final boolean left) {
		final MouseHandler handler = Bot.mouseHandler();
		if (handler == null) return false;

		final MouseTarget t;
		handler.handle(t = new MouseTarget(target, MouseTarget.DUMMY) {
			@Override
			public void execute(final MouseHandler handler) {
				Task.sleep(0, 350);
				if (filter.accept(handler.getLocation())) {
					handler.click(left ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3);
					handler.complete(this);
				}
			}
		});
		return !t.failed;
	}

	public static void drag(final Point p1, final Point p2, final boolean left) {
		drag(p1, p2, left ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3);
	}

	public static void drag(final Point p1, final Point p2, final int button) {
		final MouseHandler handler = Bot.mouseHandler();
		if (handler == null) return;

		Point loc = handler.getLocation();
		if (!loc.equals(p1)) move(p1);
		handler.press(p1.x, p1.y, button);
		move(p2);
		handler.release(p2.x, p2.y, button);
	}

	public static void drag(final int x1, final int y1, final int x2, final int y2, final boolean left) {
		drag(x1, y1, x2, y2, left ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3);
	}

	public static void drag(final int x1, final int y1, final int x2, final int y2, final int button) {
		drag(new Point(x1, y1), new Point(x2, y2), button);
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

	public static void move(final int x, final int y) {
		move(new Point(x, y));
	}

	public static void move(final Point p) {
		final MouseHandler handler = Bot.mouseHandler();
		if (handler == null) return;

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
		handler.handle(new MouseTarget(targetable, MouseTarget.DUMMY) {
			@Override
			public void execute(final MouseHandler handler) {
				handler.complete(this);
			}
		});
	}
}
