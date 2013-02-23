package org.powerbot.core;

import java.util.ArrayList;
import java.util.List;

import org.powerbot.core.bot.handlers.ScriptHandler;
import org.powerbot.core.event.EventManager;
import org.powerbot.core.event.EventMulticaster;
import org.powerbot.core.script.job.Job;
import org.powerbot.core.script.job.LoopTask;
import org.powerbot.core.script.job.Task;
import org.powerbot.core.script.methods.Game;
import org.powerbot.game.bot.Context;
import org.powerbot.game.bot.handler.input.MouseExecutor;
import org.powerbot.gui.BotChrome;

public class BotComposite {//TODO remove the use of a composite ... export data elsewhere
	private final Bot bot;
	MouseExecutor executor;
	EventManager eventManager;
	ScriptHandler scriptHandler;
	Context context;

	public BotComposite(final Bot bot) {
		this.bot = bot;

		executor = null;
		eventManager = new EventMulticaster();
		scriptHandler = new ScriptHandler(eventManager);
	}

	public void reload() {//TODO re-evaluate re-load method
		Bot.log.info("Refreshing environment");
		if (scriptHandler != null && scriptHandler.isActive()) {
			scriptHandler.pause();

			final Job[] jobs = scriptHandler.getScriptContainer().enumerate();
			final List<LoopTask> loopTasks = new ArrayList<>();
			for (final Job job : jobs) {
				if (job instanceof LoopTask) loopTasks.add((LoopTask) job);
			}
			final long mark = System.currentTimeMillis();
			for (final LoopTask task : loopTasks) {
				while (!task.isPaused() && task.isAlive() && System.currentTimeMillis() - mark < 120000) {
					Task.sleep(1000);
				}
			}
		}

		bot.terminateApplet();
		bot.resize(BotChrome.PANEL_WIDTH, BotChrome.PANEL_HEIGHT);

		new Thread(bot.threadGroup, Bot.instance()).start();
		BotChrome.getInstance().panel.setBot(bot);
		while (Bot.client() == null || Game.getClientState() == -1) Task.sleep(1000);
		if (scriptHandler != null && scriptHandler.isActive()) {
			scriptHandler.resume();
		}

		bot.refreshing = false;
	}
}
