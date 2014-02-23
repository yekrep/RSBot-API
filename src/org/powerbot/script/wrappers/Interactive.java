package org.powerbot.script.wrappers;

import java.awt.Point;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import org.powerbot.script.lang.ChainingIterator;
import org.powerbot.script.lang.Filter;
import org.powerbot.script.methods.Menu;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.methods.MethodProvider;
import org.powerbot.script.util.Condition;
import org.powerbot.util.math.Vector3;

public abstract class Interactive extends MethodProvider implements Targetable, Validatable {
	protected final AtomicReference<BoundingModel> boundingModel;

	public Interactive(final MethodContext ctx) {
		super(ctx);
		boundingModel = new AtomicReference<BoundingModel>(null);
	}

	public boolean isInViewport() {
		return ctx.game.isPointInViewport(getInteractPoint());
	}

	/**
	 * @see {@link #isInViewport()}
	 */
	@Deprecated
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
	public static Filter<Interactive> areOnScreen() {
		return areInViewport();
	}

	public boolean hover() {
		return isValid() && ctx.mouse.move(this);
	}

	public boolean click() {
		return click(true);
	}

	public boolean click(final boolean left) {
		return isValid() && ctx.mouse.click(this, left);
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
				return Condition.wait(new Callable<Boolean>() {
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

	public final void setBounds(final Vector3 start, final Vector3 end) {
		setBounds(start.x, end.x, start.y, end.y, start.z, end.z);
	}

	public abstract void setBounds(final int x1, final int x2, final int y1, final int y2, final int z1, final int z2);

	@Override
	public boolean isValid() {
		return true;
	}
}
