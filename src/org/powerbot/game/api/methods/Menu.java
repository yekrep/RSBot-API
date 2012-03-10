package org.powerbot.game.api.methods;

import org.powerbot.game.bot.Bot;

/**
 * @author Timer
 */
public class Menu {
	public static int getX() {
		final Bot bot = Bot.resolve();
		return bot.client.getMenuX() * bot.multipliers.GLOBAL_MENUX;
	}

	public static int getY() {
		final Bot bot = Bot.resolve();
		return bot.client.getMenuY() * bot.multipliers.GLOBAL_MENUY;
	}

	public static int getSubX() {
		final Bot bot = Bot.resolve();
		return bot.client.getSubMenuX() * bot.multipliers.GLOBAL_SUBMENUX;
	}

	public static int getSubY() {
		final Bot bot = Bot.resolve();
		return bot.client.getSubMenuY() * bot.multipliers.GLOBAL_SUBMENUY;
	}

	public static int getWidth() {
		final Bot bot = Bot.resolve();
		return bot.client.getMenuWidth() * bot.multipliers.GLOBAL_MENUWIDTH;
	}

	public static int getHeight() {
		final Bot bot = Bot.resolve();
		return bot.client.getMenuHeight() * bot.multipliers.GLOBAL_MENUHEIGHT;
	}

	public static int getSubWidth() {
		final Bot bot = Bot.resolve();
		return bot.client.getSubMenuWidth() * bot.multipliers.GLOBAL_SUBMENUWIDTH;
	}

	public static boolean isOpen() {
		return Bot.resolve().client.isMenuOpen();
	}
}
