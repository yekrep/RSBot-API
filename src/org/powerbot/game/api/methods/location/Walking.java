package org.powerbot.game.api.methods.location;

import org.powerbot.game.api.methods.Game;
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
	/**
	 * @return The x destination your character is headed for.
	 */
	public static int getDestinationX() {
		final Bot bot = Bot.resolve();
		return Game.getBaseX() + bot.client.getDestX() * bot.multipliers.GLOBAL_DESTX;
	}

	/**
	 * @return The y destination your character is headed for.
	 */
	public static int getDestinationY() {
		final Bot bot = Bot.resolve();
		return Game.getBaseY() + bot.client.getDestY() * bot.multipliers.GLOBAL_DESTY;
	}

	/**
	 * @param plane The plane of which to determine the collision offset for.
	 * @return The <code>Tile</code> of the offset location (different than map base!).
	 */
	public static Tile getCollisionOffset(final int plane) {
		final Bot bot = Bot.resolve();
		final Object groundDataInts = ((RSGroundDataInts) ((Object[]) ((RSGroundInfoRSGroundArray) bot.client.getRSGroundInfo()).getRSGroundInfoRSGroundArray())[plane]).getRSGroundDataInts();
		return new Tile(((RSGroundDataX) groundDataInts).getRSGroundDataX() * bot.multipliers.GROUNDDATA_X, ((RSGroundDataY) groundDataInts).getRSGroundDataY() * bot.multipliers.GROUNDDATA_Y, plane);
	}

	/**
	 * @param plane The plane of which to provide the collision flags for.
	 * @return The collision flags of the current map block.
	 */
	public static int[][] getCollisionFlags(final int plane) {
		return (int[][]) ((RSGroundDataBlocks) ((Object[]) ((RSGroundInfoRSGroundArray) Bot.resolve().client.getRSGroundInfo()).getRSGroundInfoRSGroundArray())[plane]).getRSGroundDataBlocks();
	}
}
