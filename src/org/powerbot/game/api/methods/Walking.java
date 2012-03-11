package org.powerbot.game.api.methods;

import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.bot.Bot;
import org.powerbot.game.client.RSGroundDataBlocks;
import org.powerbot.game.client.RSGroundDataInts;
import org.powerbot.game.client.RSGroundDataX;
import org.powerbot.game.client.RSGroundDataY;
import org.powerbot.game.client.RSGroundInfoRSGroundArray;

/**
 * @author Timer
 */
public class Walking {
	public static int getDestinationX() {
		final Bot bot = Bot.resolve();
		return bot.client.getDestX() * bot.multipliers.GLOBAL_DESTX;
	}

	public static int getDestinationY() {
		final Bot bot = Bot.resolve();
		return bot.client.getDestY() * bot.multipliers.GLOBAL_DESTY;
	}

	public static Tile getCollisionOffset(final int plane) {
		final Bot bot = Bot.resolve();
		final Object groundDataInts = ((RSGroundDataInts) ((Object[]) ((RSGroundInfoRSGroundArray) bot.client.getRSGroundInfo()).getRSGroundInfoRSGroundArray())[plane]).getRSGroundDataInts();
		return new Tile(((RSGroundDataX) groundDataInts).getRSGroundDataX() * bot.multipliers.GROUNDDATA_X, ((RSGroundDataY) groundDataInts).getRSGroundDataY() * bot.multipliers.GROUNDDATA_Y, plane);
	}

	public static int[][] getCollisionFlags(final int plane) {
		return (int[][]) ((RSGroundDataBlocks) ((Object[]) ((RSGroundInfoRSGroundArray) Bot.resolve().client.getRSGroundInfo()).getRSGroundInfoRSGroundArray())[plane]).getRSGroundDataBlocks();
	}
}
