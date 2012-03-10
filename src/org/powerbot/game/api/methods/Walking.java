package org.powerbot.game.api.methods;

import org.powerbot.game.bot.Bot;

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
}
