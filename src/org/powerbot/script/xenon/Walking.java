package org.powerbot.script.xenon;

import org.powerbot.bot.Bot;
import org.powerbot.game.client.Client;
import org.powerbot.script.xenon.wrappers.CollisionMap;
import org.powerbot.script.xenon.wrappers.Locatable;
import org.powerbot.script.xenon.wrappers.Tile;
import org.powerbot.script.xenon.wrappers.TilePath;

public class Walking {
	private static final int WIDGET = 750;
	private static final int COMPONENT_RUN_ENERGY = 6;
	private static final int SETTING_RUN_ENABLED = 463;

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
		return Settings.get(SETTING_RUN_ENABLED) == 0x1;
	}

	public static int getEnergy() {
		final Component c = Widgets.get(WIDGET, COMPONENT_RUN_ENERGY);
		if (c != null && c.isValid()) try {
			final String text = c.getText();
			if (text != null) return Integer.parseInt(text.trim());
		} catch (final NumberFormatException ignored) {
		}
		return -1;
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
