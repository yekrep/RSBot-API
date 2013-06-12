package org.powerbot.bot;

import org.powerbot.gui.BotChrome;
import org.powerbot.script.internal.ScriptHandler;
import org.powerbot.script.util.Delay;

public class BotComposite {//TODO remove the use of a composite ... export data elsewhere
	private final Bot bot;

	protected BotComposite(final Bot bot) {
		this.bot = bot;
	}

	public void reload() {//TODO re-evaluate re-load method
		bot.log.info("Refreshing environment");
		final ScriptHandler container = bot.getScriptController();
		if (container != null) {
			container.suspend();
		}

		bot.terminateApplet();
		bot.resize(BotChrome.PANEL_WIDTH, BotChrome.PANEL_HEIGHT);

		new Thread(bot.threadGroup, bot).start();
		while (bot.getClientFactory().getClient() == null || bot.getClientFactory().game.getClientState() == -1) {
			Delay.sleep(1000);
		}
		if (container != null) {
			container.resume();
		}

		bot.refreshing.set(false);
	}
}
