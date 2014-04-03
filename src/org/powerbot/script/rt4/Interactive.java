package org.powerbot.script.rt4;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.concurrent.Callable;

import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.Targetable;
import org.powerbot.script.Validatable;
import org.powerbot.script.Viewport;

public abstract class Interactive extends ClientAccessor implements Targetable, Validatable, Viewport {
	public Interactive(final ClientContext ctx) {
		super(ctx);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean inViewport() {
		return ctx.game.pointInViewport(nextPoint());
	}

	public static Filter<Interactive> areInViewport() {
		return new Filter<Interactive>() {
			@Override
			public boolean accept(final Interactive interactive) {
				return interactive.inViewport();
			}
		};
	}

	public abstract Point centerPoint();

	public final boolean hover() {
		return valid() && ctx.mouse.apply(this, new Filter<Point>() {
			@Override
			public boolean accept(final Point point) {
				return true;
			}
		});
	}

	public final boolean click() {
		return valid() && ctx.mouse.apply(this, new Filter<Point>() {
			@Override
			public boolean accept(final Point point) {
				return ctx.mouse.click(true);
			}
		});
	}

	public final boolean click(final boolean left) {
		return valid() && ctx.mouse.apply(this, new Filter<Point>() {
			@Override
			public boolean accept(final Point point) {
				return ctx.mouse.click(left);
			}
		});
	}

	public final boolean click(final int button) {
		return valid() && ctx.mouse.apply(this, new Filter<Point>() {
			@Override
			public boolean accept(final Point point) {
				return ctx.mouse.click(button);
			}
		});
	}

	public boolean click(final String action) {
		return click(Menu.filter(action));
	}

	public boolean click(final String action, final String option) {
		return click(Menu.filter(action, option));
	}

	public final boolean click(final Filter<Menu.Command> f) {
		return valid() && ctx.mouse.apply(this, new Filter<Point>() {
			@Override
			public boolean accept(final Point point) {
				return Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() {
						return ctx.menu.indexOf(f) == 0;
					}
				}, 5, 10) && ctx.mouse.click(true);
			}
		});
	}

	public boolean interact(final String action) {
		return interact(true, action);
	}

	public boolean interact(final String action, final String option) {
		return interact(true, action, option);
	}

	public final boolean interact(final Filter<Menu.Command> f) {
		return interact(true, f);
	}

	public boolean interact(final boolean auto, final String action) {
		return interact(auto, Menu.filter(action));
	}

	public boolean interact(final boolean auto, final String action, final String option) {
		return interact(auto, Menu.filter(action, option));
	}

	public final boolean interact(final boolean auto, final Filter<Menu.Command> f) {
		if (!valid()) {
			return false;
		}
		final Filter<Point> f_auto = new Filter<Point>() {
			@Override
			public boolean accept(final Point point) {
				return Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() {
						return ctx.menu.indexOf(f) != -1;
					}
				}, 15, 10);
			}
		};

		Rectangle r = new Rectangle(-1, -1, -1, -1);
		for (int i = 0; i < 3; i++) {
			final Rectangle c = r;
			if (!ctx.mouse.apply(this, auto ? f_auto : new Filter<Point>() {
				@Override
				public boolean accept(final Point point) {
					return !(c.contains(point) && ctx.menu.opened()) && ctx.mouse.click(false) && Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() {
							return ctx.menu.opened() && !ctx.menu.bounds().equals(c);
						}
					}, 20, 10);
				}
			})) {
				continue;
			}

			if (ctx.menu.click(f)) {
				return true;
			}
			r = ctx.menu.bounds();
			if (auto || r.contains(nextPoint())) {
				ctx.menu.close();
			}
		}
		ctx.menu.close();
		return false;
	}

	@Override
	public boolean valid() {
		return true;
	}
}
