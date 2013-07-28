package org.powerbot.script.wrappers;

import java.awt.Point;

import org.powerbot.script.lang.Filter;
import org.powerbot.script.lang.Targetable;
import org.powerbot.script.lang.Validatable;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.methods.MethodProvider;
import org.powerbot.script.util.Delay;

public abstract class Interactive extends MethodProvider implements Targetable, Validatable {
	private static final int ATTEMPTS = 5;

	public Interactive(MethodContext ctx) {
		super(ctx);
	}

	public boolean isOnScreen() {
		return ctx.game.isPointOnScreen(getInteractPoint());
	}

	public boolean hover() {
		if (!isValid()) {
			return false;
		}
		return ctx.mouse.move(this);
	}

	public boolean click() {
		return click(true);
	}

	public boolean click(final boolean left) {
		if (!isValid()) {
			return false;
		}
		return ctx.mouse.click(this, left);
	}

	public boolean interact(final String action) {
		return interact(action, null);
	}

	public boolean interact(final String action, final String option) {
		if (!isValid()) {
			return false;
		}
		int a = 0;
		while (a++ < ATTEMPTS) {
			if (!isValid()) {
				return false;
			}
			if (!ctx.mouse.move(this, new Filter<Point>() {
				@Override
				public boolean accept(final Point point) {
					if (contains(point) && ctx.menu.indexOf(action, option) != -1) {
						Delay.sleep(0, 80);
						return contains(point) && ctx.menu.indexOf(action, option) != -1;
					}
					return false;
				}
			})) {
				continue;
			}

			if (ctx.menu.click(action, option)) {
				return true;
			}
			ctx.menu.close();
		}
		return false;
	}

	@Override
	public boolean isValid() {
		return true;
	}
}
