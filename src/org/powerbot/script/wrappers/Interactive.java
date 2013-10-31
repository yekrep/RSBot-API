package org.powerbot.script.wrappers;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.util.concurrent.Callable;

import org.powerbot.script.lang.ChainingIterator;
import org.powerbot.script.lang.Filter;
import org.powerbot.script.methods.Game;
import org.powerbot.script.methods.Menu;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.methods.MethodProvider;
import org.powerbot.script.util.Condition;
import org.powerbot.script.util.Random;

public abstract class Interactive extends MethodProvider implements Targetable, Validatable {
	public Interactive(MethodContext ctx) {
		super(ctx);
	}

	public boolean isOnScreen() {
		return ctx.game.isPointOnScreen(getInteractPoint());
	}

	public static Filter<Interactive> areOnScreen() {
		return new Filter<Interactive>() {
			@Override
			public boolean accept(final Interactive interactive) {
				return interactive.isOnScreen();
			}
		};
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

	public boolean click(boolean left) {
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

	public boolean interact(String action) {
		return interact(Menu.filter(action));
	}

	public boolean interact(String action, String option) {
		return interact(Menu.filter(action, option));
	}

	public boolean interact(final Filter<Menu.Entry> f) {
		if (!isValid()) {
			return false;
		}

		TileMatrix m = ctx.players.local().getLocation().getMatrix(ctx);
		boolean a = overshoot(m);

		final Filter<Point> f2 = new Filter<Point>() {
			@Override
			public boolean accept(Point p) {
				return ctx.menu.indexOf(f) != -1 && contains(p);
			}
		};

		if (ctx.mouse.move(this, f2) && ctx.menu.click(f)) {
			return true;
		}

		if (a) {
			correct(m);
		}

		ctx.menu.close();
		return false;
	}

	@Override
	public boolean isValid() {
		return true;
	}

	private boolean overshoot(TileMatrix matrix) {
		boolean r = false;
		if (this instanceof Renderable) {
			for (; antipattern(this, (Renderable) this); ) {
				r = true;
				if (Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return ctx.game.getCrosshair() == Game.Crosshair.ACTION;
					}
				}, 10, 20)) {
					matrix.interact("Walk here");
				}
			}
		}
		return r;
	}

	private boolean antipattern(Targetable targetable, Renderable renderable) {
		if (!ctx.antipatterns.isEnabled()) {
			return false;
		}

		Model model = renderable.getModel();
		Point mousePoint = ctx.mouse.getLocation();
		Point interactPoint = targetable.getInteractPoint();
		if (model == null || !ctx.game.isPointOnScreen(interactPoint)) {
			return false;
		}

		Area area = new Area();
		for (Polygon triangle : model.getTriangles()) {
			area.add(new Area(triangle));
		}
		Rectangle rect = area.getBounds();
		if (rect.contains(interactPoint)) {
			double dist = mousePoint.distance(interactPoint);

			int w = rect.width, h = rect.height;
			int avg = (w + h) >> 1;
			int max = Math.max(w, h);
			if (dist >= avg && (max < Random.nextInt(30, 60) ? Random.nextInt(0, 3) > 0 : Random.nextBoolean()) &&
					(!ctx.players.local().isInMotion() || Random.nextBoolean())) {
				dist += Random.nextInt(-max, max);

				int x;
				int y;
				double theta = Math.atan2(interactPoint.y - mousePoint.y, interactPoint.x - mousePoint.x);
				x = mousePoint.x + (int) (dist * Math.cos(theta));
				y = mousePoint.y + (int) (dist * Math.sin(theta));

				if (ctx.game.isPointOnScreen(x, y) && ctx.mouse.move(x, y) &&
						ctx.menu.indexOf(Menu.filter("Walk here")) == 0 && ctx.mouse.click(true)) {
					return true;
				}
			}
		}
		return false;
	}

	private void correct(TileMatrix c) {
		Tile tileOfInteractive = Tile.NIL;
		if (this instanceof Locatable) {
			tileOfInteractive = ((Locatable) this).getLocation();
		}
		Tile dest = ctx.movement.getDestination();
		int l_d = ctx.movement.getDistance(c, dest);
		int l_t = ctx.movement.getDistance(c, tileOfInteractive);
		if ((l_d < 0 && dest.getMatrix(ctx).isValid()) ||
				(l_t != -1 && l_d != -1 && l_d > l_t + 2)) {
			if (!(c.isOnScreen() && c.interact("Walk here"))) {
				ctx.movement.stepTowards(c);
			}
		}
	}
}
