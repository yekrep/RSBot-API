package org.powerbot.script.wrappers;

import java.awt.Point;
import java.util.concurrent.Callable;

import org.powerbot.script.lang.ChainingIterator;
import org.powerbot.script.lang.Filter;
import org.powerbot.script.methods.Menu;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.methods.MethodProvider;
import org.powerbot.script.util.Condition;

public abstract class Interactive extends MethodProvider implements Targetable, Validatable {
	public Interactive(final MethodContext ctx) {
		super(ctx);
	}

	public boolean isInViewport() {
		return ctx.game.isPointInViewport(getInteractPoint());
	}

	/**
	 * @see {@link #isInViewport()}
	 */
	@Deprecated
	@SuppressWarnings("unused")
	public boolean isOnScreen() {
		return isInViewport();
	}

	public static Filter<Interactive> areInViewport() {
		return new Filter<Interactive>() {
			@Override
			public boolean accept(final Interactive interactive) {
				return interactive.isInViewport();
			}
		};
	}

	@Deprecated
	@SuppressWarnings("unused")
	public static Filter<Interactive> areOnScreen() {
		return areInViewport();
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

	public static ChainingIterator<Interactive> doInteract(final String action) {
		return new ChainingIterator<Interactive>() {
			@Override
			public boolean next(final int index, final Interactive item) {
				return item.interact(action);
			}
		};
	}

	public static ChainingIterator<Interactive> doInteract(final String action, final String option) {
		return new ChainingIterator<Interactive>() {
			@Override
			public boolean next(final int index, final Interactive item) {
				return item.interact(action, option);
			}
		};
	}

	public boolean interact(final String action) {
		return interact(Menu.filter(action));
	}

	public boolean interact(final String action, final String option) {
		return interact(Menu.filter(action, option));
	}

	public boolean interact(final Filter<Menu.Entry> f) {
		if (!isValid()) {
			return false;
		}

		final Filter<Point> f2 = new Filter<Point>() {
			@Override
			public boolean accept(final Point p) {
				return contains(p) && Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() {
						return ctx.menu.indexOf(f) != -1;
					}
				}, 15, 10);
			}
		};

		if (ctx.mouse.move(this, f2) && ctx.menu.click(f)) {
			return true;
		}

		ctx.menu.close();
		return false;
	}

	@Override
	public boolean isValid() {
		return true;
	}
}
