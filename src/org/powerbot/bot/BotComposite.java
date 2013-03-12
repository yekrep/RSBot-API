package org.powerbot.bot;

import org.powerbot.game.bot.Context;
import org.powerbot.gui.BotChrome;
import org.powerbot.script.internal.ScriptManager;
import org.powerbot.script.xenon.Game;
import org.powerbot.script.xenon.util.Delay;

public class BotComposite {//TODO remove the use of a composite ... export data elsewhere
	private final Bot bot;
	Context context;

	protected BotComposite(final Bot bot) {
		this.bot = bot;
	}

	public void reload() {//TODO re-evaluate re-load method
		Bot.log.info("Refreshing environment");
		final ScriptManager container = bot.getScriptController();
		if (container != null) {
			container.suspend();
		}

		bot.terminateApplet();
		bot.resize(BotChrome.PANEL_WIDTH, BotChrome.PANEL_HEIGHT);

		new Thread(bot.threadGroup, Bot.instance()).start();
		BotChrome.getInstance().panel.setBot(bot);
		while (Bot.client() == null || Game.getClientState() == -1) Delay.sleep(1000);
		if (container != null) {
			container.resume();
		}

		bot.refreshing = false;
	}
}
