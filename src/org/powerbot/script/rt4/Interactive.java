package org.powerbot.script.rt4;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

	/**
	 * Hovers the target and compensates for movement.
	 *
	 * @return <tt>true</tt> if the mouse is within the target; otherwise <tt>false</tt>
	 */
	public final boolean hover() {
		return valid() && ctx.input.apply(this, new Filter<Point>() {
			@Override
			public boolean accept(final Point point) {
				return true;
			}
		});
	}

	/**
	 * Clicks the target and compensates for movement. Does not check intent or expected result (mouse cross-hair).
	 *
	 * @return <tt>true</tt> if the click was executed; otherwise <tt>false</tt>
	 */
	public final boolean click() {
		return valid() && ctx.input.apply(this, new Filter<Point>() {
			@Override
			public boolean accept(final Point point) {
				return ctx.input.click(true);
			}
		});
	}

	/**
	 * Clicks the target and compensates for movement. Does not check intent or expected result (mouse cross-hair).
	 *
	 * @param left <tt>true</tt> to click left, <tt>false</tt> to click right
	 * @return <tt>true</tt> if the click was executed; otherwise <tt>false</tt>
	 */
	public final boolean click(final boolean left) {
		return valid() && ctx.input.apply(this, new Filter<Point>() {
			@Override
			public boolean accept(final Point point) {
				return ctx.input.click(left);
			}
		});
	}

	/**
	 * Clicks the target and compensates for movement. Does not check intent or expected result (mouse cross-hair).
	 *
	 * @param button the desired mouse button to press
	 * @return <tt>true</tt> if the click was executed; otherwise <tt>false</tt>
	 */
	public final boolean click(final int button) {
		return valid() && ctx.input.apply(this, new Filter<Point>() {
			@Override
			public boolean accept(final Point point) {
				return ctx.input.click(button);
			}
		});
	}

	/**
	 * Clicks the target and compensates for movement.
	 * This method expects (and requires) that the given action is up-text (menu index 0).
	 * This method can be used when precision clicking is required, and the option is guaranteed to be up-text.
	 * WARNING: this method DOES NOT check intent or expected result (mouse cross-hair).
	 * WARNING: The return status does not guarantee the correct action was acted upon.
	 *
	 * @param action the action to look for
	 * @return <tt>true</tt> if the mouse was clicked, otherwise <tt>false</tt>
	 */
	public boolean click(final String action) {
		return click(Menu.filter(action));
	}

	/**
	 * Clicks the target and compensates for movement.
	 * This method expects (and requires) that the given action is up-text (menu index 0).
	 * This method can be used when precision clicking is required, and the option is guaranteed to be up-text.
	 * WARNING: this method DOES NOT check intent or expected result (mouse cross-hair).
	 * WARNING: The return status does not guarantee the correct action was acted upon.
	 *
	 * @param action the action to look for
	 * @param option the option to look for
	 * @return <tt>true</tt> if the mouse was clicked, otherwise <tt>false</tt>
	 */
	public boolean click(final String action, final String option) {
		return click(Menu.filter(action, option));
	}

	/**
	 * Clicks the target and compensates for movement.
	 * This method expects (and requires) that the given action is up-text (menu index 0).
	 * This method can be used when precision clicking is required, and the option is guaranteed to be up-text.
	 * WARNING: this method DOES NOT check intent or expected result (mouse cross-hair).
	 * WARNING: The return status does not guarantee the correct action was acted upon.
	 *
	 * @param f the menu command to look for
	 * @return <tt>true</tt> if the mouse was clicked, otherwise <tt>false</tt>
	 */
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

	/**
	 * Interacts with the target and compensates for movement.
	 * This method will interact (and choose it) when it finds the desired action.
	 * This method accomplishes it via left or right click.
	 * WARNING: this method DOES NOT check intent or expected result (mouse cross-hair).
	 * WARNING: The return status does not guarantee the correct action was acted upon.
	 *
	 * @param action the action to look for
	 * @return <tt>true</tt> if the mouse was clicked, otherwise <tt>false</tt>
	 */
	public boolean interact(final String action) {
		return interact(true, action);
	}

	/**
	 * Interacts with the target and compensates for movement.
	 * This method will interact (and choose it) when it finds the desired action.
	 * This method accomplishes it via left or right click.
	 * WARNING: this method DOES NOT check intent or expected result (mouse cross-hair).
	 * WARNING: The return status does not guarantee the correct action was acted upon.
	 *
	 * @param action the action to look for
	 * @param option the option to look for
	 * @return <tt>true</tt> if the mouse was clicked, otherwise <tt>false</tt>
	 */
	public boolean interact(final String action, final String option) {
		return interact(true, action, option);
	}

	/**
	 * Interacts with the target and compensates for movement.
	 * This method will interact (and choose it) when it finds the desired action.
	 * This method accomplishes it via left or right click.
	 * WARNING: this method DOES NOT check intent or expected result (mouse cross-hair).
	 * WARNING: The return status does not guarantee the correct action was acted upon.
	 *
	 * @param f the menu command to look for
	 * @return <tt>true</tt> if the mouse was clicked, otherwise <tt>false</tt>
	 */
	public final boolean interact(final Filter<Menu.Command> f) {
		return interact(true, f);
	}

	/**
	 * Interacts with the target and compensates for movement.
	 * This method will interact (and choose it) when it finds the desired action.
	 * This method accomplishes it via left or right click (as defined).
	 * When auto is set to false, the method forcibly right clicks before searching for menu options.
	 * This is useful when precision clicking is required and the option is always in the menu (not up-text).
	 * WARNING: this method DOES NOT check intent or expected result (mouse cross-hair).
	 * WARNING: The return status does not guarantee the correct action was acted upon.
	 *
	 * @param auto   <tt>true</tt> is normal behavior, <tt>false</tt> forces right click
	 * @param action the action to look for
	 * @return <tt>true</tt> if the mouse was clicked, otherwise <tt>false</tt>
	 */
	public boolean interact(final boolean auto, final String action) {
		return interact(auto, Menu.filter(action));
	}

	/**
	 * Interacts with the target and compensates for movement.
	 * This method will interact (and choose it) when it finds the desired action.
	 * This method accomplishes it via left or right click (as defined).
	 * When auto is set to false, the method forcibly right clicks before searching for menu options.
	 * This is useful when precision clicking is required and the option is always in the menu (not up-text).
	 * WARNING: this method DOES NOT check intent or expected result (mouse cross-hair).
	 * WARNING: The return status does not guarantee the correct action was acted upon.
	 *
	 * @param auto   <tt>true</tt> is normal behavior, <tt>false</tt> forces right click
	 * @param action the action to look for
	 * @param option the option to look for
	 * @return <tt>true</tt> if the mouse was clicked, otherwise <tt>false</tt>
	 */
	public boolean interact(final boolean auto, final String action, final String option) {
		return interact(auto, Menu.filter(action, option));
	}


	/**
	 * Interacts with the target and compensates for movement.
	 * This method will interact (and choose it) when it finds the desired action.
	 * This method accomplishes it via left or right click (as defined).
	 * When auto is set to false, the method forcibly right clicks before searching for menu options.
	 * This is useful when precision clicking is required and the option is always in the menu (not up-text).
	 * WARNING: this method DOES NOT check intent or expected result (mouse cross-hair).
	 * WARNING: The return status does not guarantee the correct action was acted upon.
	 *
	 * @param auto <tt>true</tt> is normal behavior, <tt>false</tt> forces right click
	 * @param f    the menu command to look for
	 * @return <tt>true</tt> if the mouse was clicked, otherwise <tt>false</tt>
	 */
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

	/**
	 * Sets the boundaries of this entity utilizing an array.
	 *
	 * @param arr {x1, x2, y1, y2, z1, z2}
	 */
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
		draw(render, -1);
	}

	@Override
	public void draw(final Graphics render, final int s_alpha) {
		final Field f;
		try {
			f = getClass().getDeclaredField("TARGET_COLOR");
		} catch (final NoSuchFieldException ignored) {
			shade(render, s_alpha);
			return;
		}
		f.setAccessible(true);
		Color c;
		try {
			c = (Color) f.get(null);
		} catch (final IllegalAccessException ignored) {
			return;
		}

		final int alpha = s_alpha == -1 ? 15 : s_alpha;
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

	private void shade(final Graphics render, final int s_alpha) {
		final Field f, f2;
		try {
			f = getClass().getDeclaredField("TARGET_STROKE_COLOR");
			f2 = getClass().getDeclaredField("TARGET_FILL_COLOR");
		} catch (final NoSuchFieldException ignored) {
			return;
		}
		f.setAccessible(true);
		f2.setAccessible(true);
		Color c, c2;
		try {
			c = (Color) f.get(null);
			c2 = (Color) f2.get(null);
		} catch (final IllegalAccessException ignored) {
			return;
		}
		final Method m;
		try {
			m = getClass().getDeclaredMethod("boundingRect");
		} catch (final NoSuchMethodException ignored) {
			return;
		}
		m.setAccessible(true);
		final Rectangle r;
		try {
			if ((r = (Rectangle) m.invoke(null)) == null) {
				throw new InvocationTargetException(new RuntimeException("Rectangle was null."));
			}
		} catch (final IllegalAccessException ignored) {
			return;
		} catch (final InvocationTargetException ignored) {
			return;
		}

		final int alpha = s_alpha == -1 ? 15 : s_alpha;
		final int rgb = c2.getRGB();
		if (((rgb >> 24) & 0xff) != alpha) {
			c2 = new Color((rgb >> 16) & 0xff, (rgb >> 8) & 0xff, rgb & 0xff, alpha);
		}

		render.setColor(c2);
		render.fillRect(r.x, r.y, r.width, r.height);
		render.setColor(c);
		render.drawRect(r.x, r.y, r.width, r.height);
	}
}
