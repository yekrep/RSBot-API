package org.powerbot.game.api.methods.location;

import java.awt.Point;

import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.methods.Settings;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.wrappers.Locatable;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.bot.Bot;
import org.powerbot.game.client.RSGroundDataBlocks;
import org.powerbot.game.client.RSGroundDataInts;
import org.powerbot.game.client.RSGroundDataX;
import org.powerbot.game.client.RSGroundDataY;
import org.powerbot.game.client.RSGroundInfoRSGroundArray;

/**
 * A utility for the manipulation of information required for waking.
 *
 * @author Timer
 */
public class Walking {
	private static final int WIDGET = 750;
	private static final int WIDGET_RUN = 2;
	private static final int WIDGET_RUN_ENERGY = 6;

	public static Tile getDestination() {
		final Bot bot = Bot.resolve();
		return new Tile(
				Game.getBaseX() + bot.getClient().getDestX() * bot.multipliers.GLOBAL_DESTX,
				Game.getBaseY() + bot.getClient().getDestY() * bot.multipliers.GLOBAL_DESTY,
				Game.getPlane()
		);
	}

	/**
	 * @param plane The plane of which to determine the collision offset for.
	 * @return The <code>Tile</code> of the offset location (different than map base!).
	 */
	public static Tile getCollisionOffset(final int plane) {
		final Bot bot = Bot.resolve();
		final Object groundDataInts = ((RSGroundDataInts) ((Object[]) ((RSGroundInfoRSGroundArray) bot.getClient().getRSGroundInfo()).getRSGroundInfoRSGroundArray())[plane]).getRSGroundDataInts();
		return new Tile(((RSGroundDataX) groundDataInts).getRSGroundDataX() * bot.multipliers.GROUNDDATA_X, ((RSGroundDataY) groundDataInts).getRSGroundDataY() * bot.multipliers.GROUNDDATA_Y, plane);
	}

	/**
	 * @param plane The plane of which to provide the collision flags for.
	 * @return The collision flags of the current map block.
	 */
	public static int[][] getCollisionFlags(final int plane) {
		return (int[][]) ((RSGroundDataBlocks) ((Object[]) ((RSGroundInfoRSGroundArray) Bot.resolve().getClient().getRSGroundInfo()).getRSGroundInfoRSGroundArray())[plane]).getRSGroundDataBlocks();
	}

	public static void setRun(final boolean enabled) {
		if (isRunEnabled() != enabled) {
			Widgets.get(WIDGET, WIDGET_RUN).click(true);
		}
	}

	public static boolean isRunEnabled() {
		return Settings.get(Settings.BOOLEAN_RUN_ENABLED) == 1;
	}

	public static int getEnergy() {
		try {
			return Integer.parseInt(Widgets.get(WIDGET, WIDGET_RUN_ENERGY).getText());
		} catch (final NumberFormatException ignored) {
			return -1;
		}
	}

	/**
	 * Clicks a tile on the minimap.
	 *
	 * @param tile The tile to click (global).
	 * @return <tt>true</tt> if the tile was clicked; otherwise <tt>false</tt>.
	 */
	public static boolean walk(final Tile tile) {
		return Mouse.apply(
				new Locatable() {
					public Point getCentralPoint() {
						return Calculations.worldToMap(tile.getX(), tile.getY());
					}

					public Point getNextViewportPoint() {
						return getCentralPoint();
					}

					public boolean contains(final Point point) {
						return getCentralPoint().distance(point) <= 2;
					}

					public boolean verify() {
						return Calculations.distance(tile, Players.getLocal().getPosition()) <= 17;
					}
				},
				new Filter<Point>() {
					public boolean accept(final Point point) {
						Mouse.click(true);
						return true;
					}
				}
		);
	}
}
