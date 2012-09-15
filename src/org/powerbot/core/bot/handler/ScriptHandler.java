package org.powerbot.core.bot.handler;

import java.util.logging.Logger;

import org.powerbot.core.bot.Bot;
import org.powerbot.core.script.Script;
import org.powerbot.core.script.job.Container;
import org.powerbot.core.script.job.TaskContainer;
import org.powerbot.service.scripts.ScriptDefinition;
import org.powerbot.util.Tracker;

/**
 * @author Timer
 */
public class ScriptHandler {
	public final Logger log = Logger.getLogger(ScriptHandler.class.getName());

	private final Bot bot;
	private Container container;

	private Script script;
	private ScriptDefinition def;
	public long started;

	public ScriptHandler(final Bot bot) {
		this.bot = bot;
		this.script = null;
	}

	public boolean start(final Script script, final ScriptDefinition def) {
		if (isActive()) {
			return false;
		}

		this.def = def;
		this.script = script;

		script.start();
		(container = new TaskContainer()).submit(new RandomHandler(bot, this));
		started = System.currentTimeMillis();
		track("");
		return true;
	}

	public void pause() {
		if (script != null) {
			script.setPaused(true);
			track("pause");
		}
	}

	public void resume() {
		if (script != null) {
			script.setPaused(false);
			track("resume");
		}
	}

	public void shutdown() {
		if (script != null) {
			container.shutdown();
			script.shutdown();
			track("stop");
		}
	}

	public void stop() {
		if (script != null) {
			container.shutdown();
			script.stop();
			track("kill");
		}
	}

	public boolean isPaused() {
		return script != null && script.isPaused();
	}

	public boolean isActive() {
		return script != null && script.isActive();
	}

	public boolean isShutdown() {
		return script != null && script.isShutdown();
	}

	public ScriptDefinition getDefinition() {
		return def;
	}

	private void track(final String action) {
		if (def == null || def.local || def.getID() == null || def.getID().isEmpty() || def.getName() == null) {
			return;
		}

		final String page = String.format("scripts/%s/%s", def.getID(), action);
		Tracker.getInstance().trackPage(page, def.getName());
	}
}
