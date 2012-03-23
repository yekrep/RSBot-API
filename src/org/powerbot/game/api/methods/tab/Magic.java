package org.powerbot.game.api.methods.tab;

import org.powerbot.game.bot.Bot;

/**
 * @author Timer
 */
public class Magic {
	public static boolean isSpellSelected() {
		return Bot.resolve().getClient().isSpellSelected();
	}
}
