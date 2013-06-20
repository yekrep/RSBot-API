package org.powerbot.script.wrappers;

import org.powerbot.script.lang.Interactable;
import org.powerbot.script.lang.Targetable;
import org.powerbot.script.lang.Validatable;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.methods.MethodProvider;
import org.powerbot.script.util.Delay;
import org.powerbot.script.lang.Filter;

import java.awt.Point;

public abstract class Interactive extends MethodProvider implements Interactable, Targetable, Validatable {
	private static final int ATTEMPTS = 5;

	public Interactive(MethodContext ctx) {
		super(ctx);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isOnScreen() {
		return ctx.game.isPointOnScreen(getInteractPoint());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hover() {
		return ctx.mouse.move(this);
	}

	public boolean click() {
		return click(true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean click(final boolean left) {
		return ctx.mouse.click(this, left);
	}

	public boolean interact(final String action) {
		return interact(action, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean interact(final String action, final String option) {
		int a = 0;
		while (a++ < ATTEMPTS) {
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
