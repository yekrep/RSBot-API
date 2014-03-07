package org.powerbot.api.os.tools;

import org.powerbot.api.ClientAccessor;
import org.powerbot.api.ClientContext;
import org.powerbot.bot.os.client.Client;

public class Movement extends ClientAccessor {
	public Movement(final ClientContext ctx) {
		super(ctx);
	}

	public Tile getDestination() {
		final Client client = ctx.client();
		if (client == null) {
			return null;
		}
		final int dX = client.getDestinationX(), dY = client.getDestinationY();
		if (dX == -1 || dY == -1) {
			return Tile.NIL;
		}
		return ctx.game.getMapOffset().derive(dX, dY);
	}

	public boolean stepTowards(final Locatable locatable) {
		Tile loc = locatable.getLocation();
		if (!loc.getMatrix(ctx).isOnMap()) {
			loc = getClosestOnMap(loc);
		}
		final Tile t = loc;
		return false;//TODO: this
	}

	public Tile getClosestOnMap(final Locatable locatable) {
		final Tile local = ctx.players.local().getLocation();
		final Tile tile = locatable.getLocation();
		if (local == Tile.NIL || tile == Tile.NIL) {
			return Tile.NIL;
		}
		if (tile.getMatrix(ctx).isOnMap()) {
			return tile;
		}
		final int x2 = local.x;
		final int y2 = local.y;
		int x1 = tile.x;
		int y1 = tile.y;
		final int dx = Math.abs(x2 - x1);
		final int dy = Math.abs(y2 - y1);
		final int sx = (x1 < x2) ? 1 : -1;
		final int sy = (y1 < y2) ? 1 : -1;
		int off = dx - dy;
		for (; ; ) {
			final Tile t = new Tile(x1, y1, local.floor);
			if (t.getMatrix(ctx).isOnMap()) {
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

	public int getEnergyLevel() {
		return 100;//TODO
	}

	public boolean isRunning() {
		return true;//TODO
	}

	public boolean setRunning(final boolean running) {
		//TODO
		return true;
	}
}
