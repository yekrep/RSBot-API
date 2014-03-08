package org.powerbot.script.os;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.concurrent.Callable;

import org.powerbot.script.Filter;
import org.powerbot.script.Condition;

public abstract class Interactive extends ClientAccessor implements Targetable, Validatable {
	public Interactive(final ClientContext ctx) {
		super(ctx);
	}

	public boolean isInViewport() {
		return ctx.game.isPointInViewport(getNextPoint());
	}

	public abstract Point getCenterPoint();

	public final boolean click(final Filter<Menu.Command> f) {
		return ctx.mouse.apply(this, new Filter<Point>() {
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

	public final boolean interact(final Filter<Menu.Command> f) {
		Rectangle r = new Rectangle(-1, -1, -1, -1);
		for (int i = 0; i < 3; i++) {
			final Rectangle c = r;
			if (!ctx.mouse.apply(this, new Filter<Point>() {
				@Override
				public boolean accept(final Point point) {
					return !(c.contains(point) && ctx.menu.isOpen()) && ctx.mouse.click(false) && Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() {
							return ctx.menu.isOpen() && !ctx.menu.getBounds().equals(c);
						}
					}, 20, 10);
				}
			})) {
				continue;
			}

			if (ctx.menu.click(f)) {
				return true;
			}
			r = ctx.menu.getBounds();
			if (r.contains(getNextPoint())) {
				ctx.menu.close();
			}
		}
		ctx.menu.close();
		return false;
	}

	@Override
	public boolean isValid() {
		return true;
	}
}
