package org.powerbot.script.xenon;

import org.powerbot.script.xenon.wrappers.Locatable;
import org.powerbot.script.xenon.wrappers.Tile;

public class Walking {
	public static boolean stepTowards(final Locatable locatable) {
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
