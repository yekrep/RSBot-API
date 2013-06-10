package org.powerbot.script.methods;

import java.awt.Point;

import org.powerbot.bot.ClientFactory;
import org.powerbot.client.Client;
import org.powerbot.script.util.Delay;
import org.powerbot.script.wrappers.Component;
import org.powerbot.script.wrappers.Locatable;
import org.powerbot.script.wrappers.Player;
import org.powerbot.script.wrappers.Targetable;
import org.powerbot.script.wrappers.Tile;
import org.powerbot.script.wrappers.TilePath;

public class Movement {
	private static final int WIDGET = 750;
	private static final int COMPONENT_RUN = 2;
	private static final int COMPONENT_RUN_ENERGY = 6;
	private static final int SETTING_RUN_ENABLED = 463;

	public static TilePath newTilePath(final Tile... tiles) {
		if (tiles == null) throw new IllegalArgumentException("tiles are null");
		return new TilePath(tiles);
	}

	public static Tile getDestination() {
		final Client client = ClientFactory.getFactory().getClient();
		if (client == null) return null;
		final int dX = client.getDestX(), dY = client.getDestY();
		if (dX == -1 || dY == -1) return null;
		final Tile base = Game.getMapBase();
		return base != null ? base.derive(dX, dY) : null;
	}

	public static boolean stepTowards(final Locatable locatable) {
		final Tile tile = locatable.getLocation();
		return Mouse.click(new Targetable() {
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

	public static boolean setRunning(final boolean run) {
		if (isRunning() != run) {
			final Component c = Widgets.get(WIDGET, COMPONENT_RUN);
			if (c != null && c.click(true)) for (int i = 0; i < 20 && isRunning() != run; i++) Delay.sleep(100, 200);
		}
		return isRunning() == run;
	}

	public static boolean isRunning() {
		return Settings.get(SETTING_RUN_ENABLED) == 0x1;
	}

	public static int getEnergyLevel() {
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

		final Tile location = World.getPlayer().getLocation();
		tile = tile.derive(-location.getX(), -location.getY());
		final double angle = Math.atan2(tile.getY(), tile.getX());
		return new Tile(
				location.getX() + (int) (16d * Math.cos(angle)),
				location.getY() + (int) (16d * Math.sin(angle)),
				tile.getPlane()
		);
	}

	public static double distance(final int x1, final int y1, final int x2, final int y2) {
		return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}

	public static double distance(final Locatable a, final Locatable b) {
		final Tile tA = a != null ? a.getLocation() : null, tB = b != null ? b.getLocation() : null;
		if (tA == null || tB == null) return Double.MAX_VALUE;
		return distance(tA.x, tA.y, tB.x, tB.y);
	}

	public static double distanceTo(final int x, final int y) {
		final Player local = World.getPlayer();
		final Tile location;
		if (local == null || (location = local.getLocation()) == null) return Double.MAX_VALUE;
		return distance(location.x, location.y, x, y);
	}

	public static double distanceTo(final Locatable locatable) {
		return distance(World.getPlayer(), locatable);
	}
}
