package org.powerbot.script.methods;

import java.awt.Point;

import org.powerbot.client.Client;
import org.powerbot.script.lang.Locatable;
import org.powerbot.script.lang.Targetable;
import org.powerbot.script.util.Delay;
import org.powerbot.script.wrappers.Component;
import org.powerbot.script.wrappers.LocalPath;
import org.powerbot.script.wrappers.Tile;
import org.powerbot.script.wrappers.TileMatrix;
import org.powerbot.script.wrappers.TilePath;

public class Movement extends MethodProvider {
	private static final int WIDGET = 750;
	private static final int COMPONENT_RUN = 2;
	private static final int COMPONENT_RUN_ENERGY = 6;
	private static final int SETTING_RUN_ENABLED = 463;

	public Movement(MethodContext factory) {
		super(factory);
	}

	public TilePath newTilePath(final Tile... tiles) {
		if (tiles == null) {
			throw new IllegalArgumentException("tiles are null");
		}
		return new TilePath(ctx, tiles);
	}

	public LocalPath findPath(Locatable locatable) {
		if (locatable == null) {
			throw new IllegalArgumentException();
		}
		return new LocalPath(ctx, ctx.map, locatable);
	}

	public Tile getDestination() {
		Client client = ctx.getClient();
		if (client == null) {
			return null;
		}
		final int dX = client.getDestX(), dY = client.getDestY();
		if (dX == -1 || dY == -1) {
			return null;
		}
		return ctx.game.getMapBase().derive(dX, dY);
	}

	public boolean stepTowards(final Locatable locatable) {
		final Tile loc = locatable.getLocation();
		return ctx.mouse.click(new Targetable() {
			private TileMatrix tile = loc.getMatrix(ctx);

			@Override
			public Point getInteractPoint() {
				return tile.getMapPoint();
			}

			@Override
			public Point getNextPoint() {
				return tile.getMapPoint();
			}

			@Override
			public Point getCenterPoint() {
				return tile.getMapPoint();
			}

			@Override
			public boolean contains(final Point point) {
				return point.distance(tile.getMapPoint()) < Math.sqrt(3);
			}
		}, true);
	}

	public boolean setRunning(final boolean run) {
		if (isRunning() != run) {
			final Component c = ctx.widgets.get(WIDGET, COMPONENT_RUN);
			if (c != null && c.click(true)) {
				for (int i = 0; i < 20 && isRunning() != run; i++) {
					Delay.sleep(100, 200);
				}
			}
		}
		return isRunning() == run;
	}

	public boolean isRunning() {
		return ctx.settings.get(SETTING_RUN_ENABLED) == 0x1;
	}

	public int getEnergyLevel() {
		final Component c = ctx.widgets.get(WIDGET, COMPONENT_RUN_ENERGY);
		if (c != null && c.isValid()) {
			try {
				final String text = c.getText();
				if (text != null) {
					return Integer.parseInt(text.trim());
				}
			} catch (final NumberFormatException ignored) {
			}
		}
		return -1;
	}

	public Tile getClosestOnMap(Tile tile) {
		if (tile.getMatrix(ctx).isOnMap()) {
			return tile;
		}

		final Tile location = ctx.players.getLocal().getLocation();
		tile = tile.derive(-location.getX(), -location.getY());
		final double angle = Math.atan2(tile.getY(), tile.getX());
		return new Tile(
				location.getX() + (int) (16d * Math.cos(angle)),
				location.getY() + (int) (16d * Math.sin(angle)),
				tile.getPlane()
		);
	}

	public int getDistance(Locatable _start, Locatable _end) {
		Tile start, end;
		if (_start == null || _end == null) {
			return -1;
		}
		start = _start.getLocation();
		end = _end.getLocation();

		Tile base = ctx.game.getMapBase();
		if (base == Tile.NIL || start == Tile.NIL || end == Tile.NIL) {
			return -1;
		}
		start = start.derive(-base.x, -base.y);
		end = end.derive(-base.x, -base.y);

		int startX = start.getX(), startY = start.getY();
		int endX = end.getX(), endY = end.getY();
		return ctx.map.getDistance(startX, startY, endX, endY, ctx.game.getPlane());
	}
}
