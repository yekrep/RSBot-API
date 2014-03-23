package org.powerbot.script.rt4;

import org.powerbot.bot.rt4.client.Client;
import org.powerbot.script.Locatable;
import org.powerbot.script.Tile;

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
		if (dX <= 0 || dY <= 0) {
			return Tile.NIL;
		}
		return ctx.game.mapOffset().derive(dX, dY);
	}

	public boolean stepTowards(final Locatable locatable) {
		Tile loc = locatable.tile();
		if (!new TileMatrix(ctx, loc).onMap()) {
			loc = getClosestOnMap(loc);
		}
		final Tile t = loc;
		return false;//TODO: this
	}

	public Tile getClosestOnMap(final Locatable locatable) {
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
