package org.powerbot.game.api.methods;

import org.powerbot.game.bot.Bot;

/**
 * @author Timer
 */
public class Magic {
	public static boolean isSpellSelected() {
		return Bot.resolve().client.isSpellSelected();
	}
}
