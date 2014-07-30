package org.powerbot.script.rt4;

import java.awt.Point;
import java.awt.Rectangle;

import org.powerbot.bot.rt4.client.Client;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.Locatable;
import org.powerbot.script.Targetable;
import org.powerbot.script.Tile;

public class Movement extends ClientAccessor {

	public Movement(final ClientContext ctx) {
		super(ctx);
	}

	/**
	 * Creates a new tile path.
	 *
	 * @param tiles The array of tiles in the path.
	 * @return the generated {@link TilePath}
	 */
	public TilePath newTilePath(final Tile... tiles) {
		if (tiles == null) {
			throw new IllegalArgumentException("tiles are null");
		}
		return new TilePath(ctx, tiles);
	}

	/**
	 * Creates a local path in the current region.
	 *
	 * @param locatable the destination tile
	 * @return the generated {@link LocalPath}
	 */
	public LocalPath findPath(final Locatable locatable) {
		if (locatable == null) {
			throw new IllegalArgumentException();
		}
		return new LocalPath(ctx, locatable);
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
			if (!new TileMatrix(ctx, loc).onMap()) {
				return false;
			}
		}
		final Tile t = loc;
		final Filter<Point> f = new Filter<Point>() {
			@Override
			public boolean accept(final Point point) {
				return ctx.input.click(true);
			}
		};
		return ctx.input.apply(new Targetable() {
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
		final Component c = ctx.widgets.widget(Constants.MOVEMENT_MAP).component(Constants.MOVEMENT_RUN_ENERGY);
		if (c != null && c.valid()) {
			try {
				return Integer.parseInt(c.text().trim());
			} catch (final NumberFormatException ignored) {
			}
		}
		return 0;
	}

	public boolean running() {
		return ctx.varpbits.varpbit(Constants.MOVEMENT_RUNNING) == 0x1;
	}

	public boolean running(final boolean running) {
		return running == running() || (ctx.widgets.widget(Constants.MOVEMENT_MAP).component(Constants.MOVEMENT_RUN_ENERGY - 1).interact("Toggle Run") &&
				Condition.wait(new Condition.Check() {
					@Override
					public boolean poll() {
						return running() == running;
					}
				}, 20, 10));
	}

	public int distance(final Locatable l1, final Locatable l2) {
		final Tile b = ctx.game.mapOffset();
		Tile t1, t2;
		if (b == null ||
				l1 == null || (t1 = l1.tile()) == null ||
				l2 == null || (t2 = l2.tile()) == null ||
				b == Tile.NIL || t1 == Tile.NIL || t2 == Tile.NIL) {
			return -1;
		}
		t1 = t1.derive(-b.x(), -b.y());
		t2 = t2.derive(-b.x(), -b.y());

		final LocalPath.Graph graph = LocalPath.getGraph(ctx);
		final LocalPath.Node[] path;
		final LocalPath.Node nodeStart, nodeStop;
		if (graph != null &&
				(nodeStart = graph.getNode(t1.x(), t1.y())) != null &&
				(nodeStop = graph.getNode(t2.x(), t2.y())) != null) {
			LocalPath.dijkstra(graph, nodeStart, nodeStop);
			path = LocalPath.follow(nodeStop);
		} else {
			path = new LocalPath.Node[0];
		}
		final int l = path.length;
		return l > 0 ? l : -1;
	}

	public int distance(final Locatable l) {
		return distance(ctx.players.local(), l);
	}

	public boolean reachable(final Locatable l1, final Locatable l2) {
		return distance(l1, l2) > 0;
	}
}
