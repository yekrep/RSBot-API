package org.powerbot.core.script.methods;

import java.awt.Point;

import org.powerbot.core.Bot;
import org.powerbot.core.script.internal.input.MouseHandler;
import org.powerbot.core.script.internal.input.MouseTarget;
import org.powerbot.core.script.wrappers.Targetable;

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
		handler.handle(new MouseTarget(targetable) {
			@Override
			public boolean accept(final Point point) {
				return true;
			}

			@Override
			public void execute(final MouseHandler handler) {
				handler.complete(this);
			}
		});
	}
}
