package org.powerbot.script.rs3;

import java.awt.Point;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.Targetable;
import org.powerbot.script.Validatable;
import org.powerbot.script.Viewport;

public abstract class Interactive extends ClientAccessor implements Targetable, Validatable, Viewport {
	protected final AtomicReference<BoundingModel> boundingModel;

	public Interactive(final ClientContext ctx) {
		super(ctx);
		boundingModel = new AtomicReference<BoundingModel>(null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean inViewport() {
		return ctx.game.isPointInViewport(nextPoint());
	}

	public static Filter<Interactive> areInViewport() {
		return new Filter<Interactive>() {
			@Override
			public boolean accept(final Interactive interactive) {
				return interactive.inViewport();
			}
		};
	}

	public boolean hover() {
		return valid() && ctx.mouse.apply(this, new Filter<Point>() {
			@Override
			public boolean accept(final Point point) {
				return true;
			}
		});
	}

	public boolean click() {
		return click(true);
	}

	public boolean click(final boolean left) {
		return hover() && ctx.mouse.click(left);
	}

	public static Filter<Interactive> doInteract(final String action) {
		return new Filter<Interactive>() {
			@Override
			public boolean accept(final Interactive item) {
				return item.interact(action);
			}
		};
	}

	public static Filter<Interactive> doInteract(final String action, final String option) {
		return new Filter<Interactive>() {
			@Override
			public boolean accept(final Interactive item) {
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
		if (!valid()) {
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

		if (ctx.mouse.apply(this, f2) && ctx.menu.click(f)) {
			return true;
		}

		ctx.menu.close();
		return false;
	}

	public final void setBounds(final int[] arr) {
		if (arr == null || arr.length != 6) {
			throw new IllegalArgumentException("length is not 6 (x1, x2, y1, y2, z1, z2)");
		}
		setBounds(arr[0], arr[1], arr[2], arr[3], arr[4], arr[5]);
	}

	public abstract void setBounds(final int x1, final int x2, final int y1, final int y2, final int z1, final int z2);

	public static Filter<Interactive> doSetBounds(final int[] arr) {
		return new Filter<Interactive>() {
			@Override
			public boolean accept(final Interactive item) {
				item.setBounds(arr);
				return true;
			}
		};
	}

	@Override
	public boolean valid() {
		return true;
	}
}
