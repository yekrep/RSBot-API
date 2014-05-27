package org.powerbot.script.rt4;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.lang.reflect.Field;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import org.powerbot.script.Condition;
import org.powerbot.script.Drawable;
import org.powerbot.script.Filter;
import org.powerbot.script.Targetable;
import org.powerbot.script.Validatable;
import org.powerbot.script.Viewport;

public abstract class Interactive extends ClientAccessor implements Targetable, Validatable, Viewport, Drawable {
	protected AtomicReference<BoundingModel> boundingModel;

	public Interactive(final ClientContext ctx) {
		super(ctx);
		boundingModel = new AtomicReference<BoundingModel>(null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean inViewport() {
		return ctx.game.inViewport(nextPoint());
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
		return valid() && ctx.input.apply(this, new Filter<Point>() {
			@Override
			public boolean accept(final Point point) {
				return true;
			}
		});
	}

	public final boolean click() {
		return valid() && ctx.input.apply(this, new Filter<Point>() {
			@Override
			public boolean accept(final Point point) {
				return ctx.input.click(true);
			}
		});
	}

	public final boolean click(final boolean left) {
		return valid() && ctx.input.apply(this, new Filter<Point>() {
			@Override
			public boolean accept(final Point point) {
				return ctx.input.click(left);
			}
		});
	}

	public final boolean click(final int button) {
		return valid() && ctx.input.apply(this, new Filter<Point>() {
			@Override
			public boolean accept(final Point point) {
				return ctx.input.click(button);
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
		return valid() && ctx.input.apply(this, new Filter<Point>() {
			@Override
			public boolean accept(final Point point) {
				return Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() {
						return ctx.menu.indexOf(f) == 0;
					}
				}, 5, 10) && ctx.input.click(true);
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
			if (!ctx.input.apply(this, auto ? f_auto : new Filter<Point>() {
				@Override
				public boolean accept(final Point point) {
					return !(c.contains(point) && ctx.menu.opened()) && ctx.input.click(false) && Condition.wait(new Callable<Boolean>() {
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

	public final void bounds(final int[] arr) {
		if (arr == null || arr.length != 6) {
			throw new IllegalArgumentException("length is not 6 (x1, x2, y1, y2, z1, z2)");
		}
		bounds(arr[0], arr[1], arr[2], arr[3], arr[4], arr[5]);
	}

	public abstract void bounds(final int x1, final int x2, final int y1, final int y2, final int z1, final int z2);

	public static Filter<Interactive> doSetBounds(final int[] arr) {
		return new Filter<Interactive>() {
			@Override
			public boolean accept(final Interactive item) {
				item.bounds(arr);
				return true;
			}
		};
	}

	@Override
	public boolean valid() {
		return true;
	}

	@Override
	public void draw(final Graphics render) {
		draw(render, 15);
	}

	@Override
	public void draw(final Graphics render, final int alpha) {
		final Field f;
		try {
			f = getClass().getDeclaredField("TARGET_COLOR");
		} catch (final NoSuchFieldException ignored) {
			return;
		}
		f.setAccessible(true);
		Color c;
		try {
			c = (Color) f.get(null);
		} catch (final IllegalAccessException ignored) {
			return;
		}

		final int rgb = c.getRGB();
		if (((rgb >> 24) & 0xff) != alpha) {
			c = new Color((rgb >> 16) & 0xff, (rgb >> 8) & 0xff, rgb & 0xff, alpha);
		}
		render.setColor(c);
		final BoundingModel m = boundingModel.get();
		if (m != null) {
			m.drawWireFrame(render);
		}
	}
}
