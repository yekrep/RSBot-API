package org.powerbot.game.bot.handler;

import org.powerbot.concurrent.Task;
import org.powerbot.game.bot.Bot;
import org.powerbot.game.bot.random.AntiRandom;
import org.powerbot.game.bot.random.Login;

public class RandomHandler extends Task {
	private final Bot bot;
	private final AntiRandom[] antiRandoms;

	public RandomHandler(final Bot bot) {
		this.bot = bot;
		antiRandoms = new AntiRandom[]{
				new Login()
		};
	}

	public void run() {
		while (bot.getActiveScript() != null) {
		}
	}
}
