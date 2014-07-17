package org.powerbot.script.rt6;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.concurrent.Callable;

import org.powerbot.bot.rt6.client.Client;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.Locatable;
import org.powerbot.script.Targetable;
import org.powerbot.script.Tile;

public class Movement extends ClientAccessor {
	public static final int WIDGET_MAP = 1465;
	public static final int COMPONENT_MAP = 4;
	public static final int COMPONENT_RUN = 19;
	public static final int COMPONENT_RUN_ENERGY = 20;
	public static final int SETTING_RUN_ENABLED = 463;

	public Movement(final ClientContext factory) {
		super(factory);
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
		return new LocalPath(ctx, ctx.map, locatable);
	}

	/**
	 * Determines the current destination of the player.
	 *
	 * @return the {@link Tile} destination; or {@link Tile#NIL} if there is no destination
	 */
	public Tile destination() {
		final Client client = ctx.client();
		if (client == null) {
			return Tile.NIL;
		}
		final int dX = client.getDestX(), dY = client.getDestY();
		if (dX == -1 || dY == -1) {
			return Tile.NIL;
		}
		return ctx.game.mapOffset().derive(dX, dY);
	}

	/**
	 * Steps towards the provided {@link Locatable}.
	 *
	 * @param locatable the locatable to step towards
	 * @return <tt>true</tt> if stepped; otherwise <tt>false</tt>
	 */
	public boolean step(final Locatable locatable) {
		Tile loc = locatable.tile();
		if (!new TileMatrix(ctx, loc).onMap()) {
			loc = closestOnMap(loc);
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

	/**
	 * Determines the closest tile on the map to the provided {@link Locatable}.
	 *
	 * @param locatable the {@link Locatable}
	 * @return the closest {@link Tile} on map to the provided {@link Locatable}
	 */
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

	/**
	 * Alters the running state.
	 *
	 * @param run <tt>true</tt> to run; otherwise <tt>false</tt>
	 * @return <tt>true</tt> if the state was successfully changed; otherwise <tt>false</tt>
	 */
	public boolean running(final boolean run) {
		return running() == run || (ctx.widgets.component(WIDGET_MAP, COMPONENT_RUN).click() &&
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return running() == run;
					}
				}, 300, 10));
	}

	/**
	 * Determines if the player is currently set to run.
	 *
	 * @return <tt>true</tt> if set to be running; otherwise <tt>false</tt>
	 */
	public boolean running() {
		return ctx.varpbits.varpbit(SETTING_RUN_ENABLED) == 0x1;
	}

	/**
	 * Determines the current energy level of the player.
	 *
	 * @return the current energy level
	 */
	public int energyLevel() {
		final Component c = ctx.widgets.component(WIDGET_MAP, COMPONENT_RUN_ENERGY);
		if (c != null && c.valid()) {
			try {
				return Integer.parseInt(c.text().replace('%', ' ').trim());
			} catch (final NumberFormatException ignored) {
			}
		}
		return 0;
	}

	public CollisionMap collisionMap() {
		return collisionMap(ctx.game.floor());
	}

	public CollisionMap collisionMap(final int plane) {
		final CollisionMap[] planes = ctx.map.getCollisionMaps();
		if (plane < 0 || plane >= planes.length) {
			return new CollisionMap(0, 0);
		}
		return planes[plane];
	}

	public int distance(final Locatable end) {
		return distance(ctx.players.local(), end);
	}

	/**
	 * Gets the distance between two places in the loaded game region.
	 *
	 * @param _start the start position
	 * @param _end   the end position
	 * @return the computed path distance
	 */
	public int distance(final Locatable _start, final Locatable _end) {
		Tile start, end;
		if (_start == null || _end == null) {
			return -1;
		}
		start = _start.tile();
		end = _end.tile();

		final Tile base = ctx.game.mapOffset();
		if (base == Tile.NIL || start == Tile.NIL || end == Tile.NIL) {
			return -1;
		}
		start = start.derive(-base.x(), -base.y());
		end = end.derive(-base.x(), -base.y());

		final int startX = start.x();
		final int startY = start.y();
		final int endX = end.x();
		final int endY = end.y();
		return ctx.map.getDistance(startX, startY, endX, endY, ctx.game.floor());
	}

	/**
	 * Determines if the the end position is reachable from the start position.
	 *
	 * @param _start the start position
	 * @param _end   the end position
	 * @return <tt>true</tt> if the end is reachable; otherwise <tt>false</tt>
	 */
	public boolean reachable(final Locatable _start, final Locatable _end) {
		Tile start, end;
		if (_start == null || _end == null) {
			return false;
		}
		start = _start.tile();
		end = _end.tile();

		final Tile base = ctx.game.mapOffset();
		if (base == Tile.NIL || start == Tile.NIL || end == Tile.NIL) {
			return false;
		}
		start = start.derive(-base.x(), -base.y());
		end = end.derive(-base.x(), -base.y());

		final int startX = start.x();
		final int startY = start.y();
		final int endX = end.x();
		final int endY = end.y();
		return ctx.map.getPath(startX, startY, endX, endY, ctx.game.floor()).length > 0;
	}
}
