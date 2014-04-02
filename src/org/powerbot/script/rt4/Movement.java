package org.powerbot.script.rt4;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.concurrent.Callable;

import org.powerbot.bot.rt4.client.Client;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.Locatable;
import org.powerbot.script.Targetable;
import org.powerbot.script.Tile;

public class Movement extends ClientAccessor {
	private static final int WIDGET_MAP = 548;
	private static final int COMPONENT_RUN_ENERGY = 94;
	private static final int VARPBIT_RUNNING = 173;

	public Movement(final ClientContext ctx) {
		super(ctx);
	}

	public Tile destination() {
		final Client client = ctx.client();
		if (client == null) {
			return Tile.NIL;
		}
		final int dX = client.getDestinationX(), dY = client.getDestinationY();
		if (dX <= 0 || dY <= 0) {
			return Tile.NIL;
		}
		return ctx.game.mapOffset().derive(dX, dY);
	}

	public boolean step(final Locatable locatable) {
		Tile loc = locatable.tile();
		if (!new TileMatrix(ctx, loc).onMap()) {
			loc = closestOnMap(loc);
		}
		final Tile t = loc;
		final Filter<Point> f = new Filter<Point>() {
			@Override
			public boolean accept(final Point point) {
				return ctx.mouse.click(true);
			}
		};
		return ctx.mouse.apply(new Targetable() {
			private final TileMatrix tile = new TileMatrix(ctx, t);

			@Override
			public Point nextPoint() {
				return tile.mapPoint();
			}

			@Override
			public boolean contains(final Point point) {
				final Point p = tile.mapPoint();
				final Rectangle t = new Rectangle(p.x - 2, p.y - 2, 4, 4);
				return t.contains(point);
			}
		}, f);
	}

	public Tile closestOnMap(final Locatable locatable) {
		final Tile local = ctx.players.local().tile();
		final Tile tile = locatable.tile();
		if (local == Tile.NIL || tile == Tile.NIL) {
			return Tile.NIL;
		}
		if (new TileMatrix(ctx, tile).onMap()) {
			return tile;
		}
		final int x2 = local.x();
		final int y2 = local.y();
		int x1 = tile.x();
		int y1 = tile.y();
		final int dx = Math.abs(x2 - x1);
		final int dy = Math.abs(y2 - y1);
		final int sx = (x1 < x2) ? 1 : -1;
		final int sy = (y1 < y2) ? 1 : -1;
		int off = dx - dy;
		for (; ; ) {
			final Tile t = new Tile(x1, y1, local.floor());
			if (new TileMatrix(ctx, t).onMap()) {
				return t;
			}
			if (x1 == x2 && y1 == y2) {
				break;
			}
			final int e2 = 2 * off;
			if (e2 > -dy) {
				off = off - dy;
				x1 = x1 + sx;
			}
			if (e2 < dx) {
				off = off + dx;
				y1 = y1 + sy;
			}
		}
		return Tile.NIL;
	}

	public int energyLevel() {
		final Component c = ctx.widgets.widget(WIDGET_MAP).component(COMPONENT_RUN_ENERGY);
		if (c != null && c.valid()) {
			try {
				return Integer.parseInt(c.text().trim());
			} catch (final NumberFormatException ignored) {
			}
		}
		return 0;
	}

	public boolean running() {
		return ctx.varpbits.varpbit(VARPBIT_RUNNING) == 0x1;
	}

	public boolean running(final boolean running) {
		return running() == running() || (ctx.widgets.widget(WIDGET_MAP).component(COMPONENT_RUN_ENERGY - 1).interact(Menu.filter("Toggle Run")) &&
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() {
						return running() == running;
					}
				}, 20, 10));
	}
}
