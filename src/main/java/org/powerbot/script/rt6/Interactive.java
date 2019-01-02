package org.powerbot.script.rt6;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicReference;

import org.powerbot.script.Condition;
import org.powerbot.script.Crosshair;
import org.powerbot.script.Filter;
import org.powerbot.script.MenuCommand;

/**
 * Interactive
 */
public abstract class Interactive extends ClientAccessor implements org.powerbot.script.Interactive {
	protected AtomicReference<BoundingModel> boundingModel;

	public Interactive(final ClientContext ctx) {
		super(ctx);
		boundingModel = new AtomicReference<BoundingModel>(null);
	}

	@Deprecated
	public static Filter<Interactive> areInViewport() {
		return new Filter<Interactive>() {
			@Override
			public boolean accept(final Interactive interactive) {
				return interactive.inViewport();
			}
		};
	}

	public static Filter<Interactive> doSetBounds(final int[] arr) {
		return new Filter<Interactive>() {
			@Override
			public boolean accept(final Interactive item) {
				item.bounds(arr);
				return true;
			}
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean inViewport() {
		return ctx.game.inViewport(nextPoint());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract Point centerPoint();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean hover() {
		return valid() && ctx.input.apply(this, new Filter<Point>() {
			@Override
			public boolean accept(final Point point) {
				return true;
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean click() {
		return valid() && ctx.input.apply(this, new Filter<Point>() {
			@Override
			public boolean accept(final Point point) {
				return ctx.input.click(true);
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean click(final boolean left) {
		return valid() && ctx.input.apply(this, new Filter<Point>() {
			@Override
			public boolean accept(final Point point) {
				return ctx.input.click(left);
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean click(final int button) {
		return valid() && ctx.input.apply(this, new Filter<Point>() {
			@Override
			public boolean accept(final Point point) {
				return ctx.input.click(button);
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean click(final String action) {
		return click(Menu.filter(action));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean click(final String action, final String option) {
		return click(Menu.filter(action, option));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean click(final Filter<? super MenuCommand> c) {
		return valid() && ctx.input.apply(this, new Filter<Point>() {
			@Override
			public boolean accept(final Point point) {
				return Condition.wait(new Condition.Check() {
					@Override
					public boolean poll() {
						return ctx.menu.indexOf(c) == 0 || c.accept(ctx.menu.tooltip());
					}
				}, 10, 30) && ctx.input.click(true);
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean interact(final String action) {
		return interact(true, action);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean interact(final String action, final String option) {
		return interact(true, action, option);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean interact(final Filter<? super MenuCommand> c) {
		return interact(true, c);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean interact(final boolean auto, final String action) {
		return interact(auto, Menu.filter(action));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean interact(final boolean auto, final String action, final String option) {
		return interact(auto, Menu.filter(action, option));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean interact(final boolean auto, final Filter<? super MenuCommand> f) {
		if (!valid()) {
			return false;
		}
		final Filter<Point> f_auto = new Filter<Point>() {
			@Override
			public boolean accept(final Point point) {
				return Condition.wait(new Condition.Check() {
					@Override
					public boolean poll() {
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
					return !(c.contains(point) && ctx.menu.opened()) && ctx.input.click(false) && Condition.wait(new Condition.Check() {
						@Override
						public boolean poll() {
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean click(final Crosshair result) {
		return click() && Condition.wait(new Condition.Check() {
			@Override
			public boolean poll() {
				return ctx.game.crosshair() == result;
			}
		}, 10, 5);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean click(final String action, final Crosshair result) {
		return click(action) && Condition.wait(new Condition.Check() {
			@Override
			public boolean poll() {
				return ctx.game.crosshair() == result;
			}
		}, 10, 5);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean click(final String action, final String option, final Crosshair result) {
		return click(action, option) && Condition.wait(new Condition.Check() {
			@Override
			public boolean poll() {
				return ctx.game.crosshair() == result;
			}
		}, 10, 5);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean click(final Filter<? super MenuCommand> c, final Crosshair result) {
		return click(c) && Condition.wait(new Condition.Check() {
			@Override
			public boolean poll() {
				return ctx.game.crosshair() == result;
			}
		}, 10, 5);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void bounds(final int[] arr) {
		if (arr == null || arr.length != 6) {
			throw new IllegalArgumentException("length is not 6 (x1, x2, y1, y2, z1, z2)");
		}
		bounds(arr[0], arr[1], arr[2], arr[3], arr[4], arr[5]);
	}

	/**
	 * Sets the boundaries of this entity.
	 *
	 * @param x1 min x
	 * @param x2 max x
	 * @param y1 min y
	 * @param y2 max y
	 * @param z1 min z
	 * @param z2 max z
	 */
	public abstract void bounds(final int x1, final int x2, final int y1, final int y2, final int z1, final int z2);

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
		} else {
			final Polygon[] t = triangles();
			for (final Polygon p : t) {
				render.drawPolygon(p);
			}
		}
	}

	public Polygon[] triangles() {
		final BoundingModel m = boundingModel.get();
		if (m != null) {
			return m.triangles();
		}
		return new Polygon[0];
	}

	/**
	 * Gets the current bounding model
	 *
	 * @return the bounding model
	 */
	public BoundingModel boundingModel() {
		return this.boundingModel.get();
	}

	/**
	 * Sets the bounding model
	 *
	 * @param boundingModel the new bounding model
	 */
	public void boundingModel(BoundingModel boundingModel) {
		this.boundingModel.set(boundingModel);
	}

	/**
	 * Compares and sets the bounding model
	 *
	 * @param expectedModel expected bounding model
	 * @param boundingModel updated bounding model
	 */
	public void boundingModel(BoundingModel expectedModel, BoundingModel boundingModel) {
		this.boundingModel.compareAndSet(expectedModel, boundingModel);
	}

}
