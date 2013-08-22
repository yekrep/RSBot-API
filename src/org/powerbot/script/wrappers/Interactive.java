package org.powerbot.script.wrappers;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Area;

import org.powerbot.script.lang.ChainingIterator;
import org.powerbot.script.lang.Filter;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.methods.MethodProvider;
import org.powerbot.script.util.Random;

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

	public boolean click(boolean left) {
		if (!isValid()) {
			return false;
		}
		return ctx.mouse.click(this, left);
	}

	public boolean interact(String action) {
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
			if (this instanceof Renderable) {
				if (antipattern(this, (Renderable) this)) {
					continue;
				}
			}
			if (!ctx.mouse.move(this, new Filter<Point>() {
				@Override
				public boolean accept(final Point point) {
					if (contains(point) && ctx.menu.indexOf(action, option) != -1) {
						sleep(10, 80);
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

	private boolean antipattern(Targetable targetable, Renderable renderable) {
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
						ctx.menu.indexOf("Walk here") == 0 && ctx.mouse.click(true)) {
					return true;
				}
			}
		}
		return false;
	}

	public static Filter<Interactive> areOnScreen() {
		return new Filter<Interactive>() {
			@Override
			public boolean accept(final Interactive interactive) {
				return interactive.isOnScreen();
			}
		};
	}

	public static ChainingIterator<Interactive> interactor(final String action) {
		return new ChainingIterator<Interactive>() {
			@Override
			public boolean next(final int index, final Interactive item) {
				return item.interact(action);
			}
		};
	}
}
