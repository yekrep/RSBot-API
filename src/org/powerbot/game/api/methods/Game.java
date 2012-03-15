package org.powerbot.game.api.methods;

import org.powerbot.game.api.Constants;
import org.powerbot.game.bot.Bot;
import org.powerbot.game.client.BaseInfoInts;
import org.powerbot.game.client.BaseInfoX;
import org.powerbot.game.client.BaseInfoY;
import org.powerbot.game.client.RSInfoBaseInfo;

/**
 * A utility that provides information about the game.
 *
 * @author Timer
 */
public class Game {
	/**
	 * @return The current state of the game client.
	 */
	public static int getClientState() {
		final Bot bot = Bot.resolve();
		final Constants constants = bot.constants;
		final int clientState = bot.client.getLoginIndex() * bot.multipliers.GLOBAL_LOGININDEX;
		if (clientState == constants.CLIENTSTATE_3) {
			return 3;
		} else if (clientState == constants.CLIENTSTATE_6) {
			return 6;
		} else if (clientState == constants.CLIENTSTATE_7) {
			return 7;
		} else if (clientState == constants.CLIENTSTATE_9) {
			return 9;
		} else if (clientState == constants.CLIENTSTATE_10) {
			return 10;
		} else if (clientState == constants.CLIENTSTATE_11) {
			return 11;
		} else if (clientState == constants.CLIENTSTATE_12) {
			return 12;
		}
		return -1;
	}

	public static boolean isLoggedIn() {
		final int state = getClientState();
		return state == 11 || state == 12;
	}

	/**
	 * @return The floor level, or plane, you are currently located on.
	 */
	public static int getFloor() {
		final Bot bot = Bot.resolve();
		return bot.client.getPlane() * bot.multipliers.GLOBAL_PLANE;
	}

	public static int getBaseX() {
		final Bot bot = Bot.resolve();
		return (((BaseInfoX) ((BaseInfoInts) ((RSInfoBaseInfo) bot.client.getRSGroundInfo()).getRSInfoBaseInfo()).getBaseInfoInts()).getBaseInfoX() * bot.multipliers.BASEDATA_X) >> 8;
	}

	public static int getBaseY() {
		final Bot bot = Bot.resolve();
		return (((BaseInfoY) ((BaseInfoInts) ((RSInfoBaseInfo) bot.client.getRSGroundInfo()).getRSInfoBaseInfo()).getBaseInfoInts()).getBaseInfoY() * bot.multipliers.BASEDATA_Y) >> 8;
	}
}
