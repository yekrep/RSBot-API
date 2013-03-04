package org.powerbot.script.xenon;

import java.awt.Point;

import org.powerbot.bot.Bot;
import org.powerbot.script.internal.input.MouseHandler;
import org.powerbot.script.internal.input.MouseTarget;
import org.powerbot.core.script.job.Task;
import org.powerbot.script.xenon.util.Filter;
import org.powerbot.script.xenon.wrappers.Targetable;

public class Mouse {
	public static void hop(final int x, final int y) {
		final MouseHandler handler = Bot.mouseHandler();
		if (handler == null) return;

		handler.move(x, y);
	}

	public static void click(final boolean left) {
		final MouseHandler handler = Bot.mouseHandler();
		if (handler == null) return;

		handler.click(left);
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
					handler.click(left);
					handler.complete(this);
				}
			}
		});
		return !t.failed;
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
