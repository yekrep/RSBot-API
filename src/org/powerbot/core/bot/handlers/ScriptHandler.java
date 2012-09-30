package org.powerbot.core.bot.handlers;

import java.util.logging.Logger;

import org.powerbot.core.event.EventManager;
import org.powerbot.core.script.Script;
import org.powerbot.core.script.job.Container;
import org.powerbot.core.script.job.Job;
import org.powerbot.core.script.job.JobListener;
import org.powerbot.core.script.job.TaskContainer;
import org.powerbot.gui.BotChrome;
import org.powerbot.service.scripts.ScriptDefinition;
import org.powerbot.util.Tracker;

/**
 * @author Timer
 */
public class ScriptHandler {
	public final Logger log = Logger.getLogger(ScriptHandler.class.getName());

	final EventManager eventManager;
	private Script script;
	private ScriptDefinition def;
	public long started;

	private Container container;
	private RandomHandler randomHandler;

	public ScriptHandler(final EventManager eventManager) {
		this.eventManager = eventManager;
		this.script = null;
	}

	public boolean start(final Script script, final ScriptDefinition def) {
		/* Prevent duplicate scripts starting */
		if (isActive()) {
			return false;
		}

		/* Set definition and script */
		this.def = def;
		this.script = script;

		/* Register listener */
		script.getContainer().addListener(new JobListener() {
			private boolean stopped = false;

			@Override
			public void jobStarted(final Job job) {
				eventManager.addListener(job);
			}

			@Override
			public void jobStopped(final Job job) {
				eventManager.removeListener(job);

				if (stopped || job.equals(script)) {
					BotChrome.getInstance().toolbar.updateScriptControls();
					stopped = true;
				}
			}
		});

		/* Start the script */
		script.start();
		started = System.currentTimeMillis();

		/* Submit the random handler */
		(container = new TaskContainer()).submit(randomHandler = new RandomHandler(this));
		/* Track the script start */
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

	public RandomHandler getRandomHandler() {
		return randomHandler;
	}

	private void track(final String action) {
		if (def == null || def.local || def.getID() == null || def.getID().isEmpty() || def.getName() == null) {
			return;
		}

		final String page = String.format("scripts/%s/%s", def.getID(), action);
		Tracker.getInstance().trackPage(page, def.getName());
	}
}
