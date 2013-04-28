package org.powerbot.script.xenon;

import org.powerbot.bot.Bot;
import org.powerbot.game.client.Client;
import org.powerbot.script.xenon.wrappers.CollisionMap;
import org.powerbot.script.xenon.wrappers.Locatable;
import org.powerbot.script.xenon.wrappers.Tile;
import org.powerbot.script.xenon.wrappers.TilePath;

public class Walking {
	public static TilePath newTilePath(final Tile... tiles) {
		if (tiles == null) throw new IllegalArgumentException("tiles are null");
		return new TilePath(tiles);
	}

	public static Tile getDestination() {
		final Client client = Bot.client();
		if (client == null) return null;
		final int dX = client.getDestX(), dY = client.getDestY();
		if (dX == -1 || dY == -1) return null;
		final Tile base = Game.getMapBase();
		return base != null ? base.derive(dX, dY) : null;
	}

	public static CollisionMap getCollisionMap() {
		final Client client = Bot.client();
		if (client == null) return null;
		return new CollisionMap(client.getPlane());
	}

	public static CollisionMap getCollisionMap(final int plane) {
		return new CollisionMap(plane);
	}

	public static boolean stepTowards(final Locatable locatable) {
		final Tile tile = locatable.getLocation();
		return false;//TODO this
	}

	public static boolean setRun(final boolean run) {
		return false;//TODO this
	}

	public static boolean isRunEnabled() {
		return false;//TODO this
	}

	public static int getEnergy() {
		return -1;//TODO this

	}

	public static Tile getClosestOnMap(Tile tile) {
		if (tile.isOnMap()) {
			return tile;
		}

		final Tile location = Players.getLocal().getLocation();
		tile = tile.derive(-location.getX(), -location.getY());
		final double angle = Math.atan2(tile.getY(), tile.getX());
		return new Tile(
				location.getX() + (int) (16d * Math.cos(angle)),
				location.getY() + (int) (16d * Math.sin(angle)),
				tile.getPlane()
		);
	}
}
