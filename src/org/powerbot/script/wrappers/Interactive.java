package org.powerbot.script.wrappers;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Area;
import java.awt.geom.Line2D;

import org.powerbot.script.lang.Filter;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.methods.MethodProvider;

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

	public boolean interact(final String action, final String option) {//TODO: anti-pattern
		if (!isValid()) {
			return false;
		}

		if (this instanceof Renderable) {
			Model model = ((Renderable) this).getModel();
			if (model != null) {
				java.awt.geom.Area area = new Area();
				for (Polygon p : model.getTriangles()) {
					area.add(new Area(p));
				}

				Point p = getInteractPoint();
				if (ctx.game.isPointOnScreen(p) && area.contains(p)) {

				}
			}
		}

		int a = 0;
		while (a++ < ATTEMPTS) {
			if (!isValid()) {
				return false;
			}
			if (this instanceof Renderable) {
				antiPattern(this, (Renderable) this);
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

	private void antiPattern(Targetable targetable, Renderable renderable) {
		Model model = renderable.getModel();
		Point m = ctx.mouse.getLocation();
		Point p = targetable.getInteractPoint();
		if (model == null || !ctx.game.isPointOnScreen(p)) {
			return;
		}

		Area a = new Area();
		for (Polygon t : model.getTriangles()) {
			a.add(new Area(t));
		}

		if (a.contains(p)) {
			double theta = Math.atan2(p.x - m.x, p.y - m.y);

		}
	}
}
