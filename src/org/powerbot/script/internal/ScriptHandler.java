package org.powerbot.script.internal;

import java.util.logging.Logger;

import org.powerbot.core.script.Script;
import org.powerbot.core.script.job.Container;
import org.powerbot.core.script.job.Job;
import org.powerbot.core.script.job.JobListener;
import org.powerbot.core.script.job.TaskContainer;
import org.powerbot.event.EventMulticaster;
import org.powerbot.gui.BotChrome;
import org.powerbot.script.internal.randoms.AntiRandom;
import org.powerbot.script.internal.randoms.BankPin;
import org.powerbot.script.internal.randoms.Login;
import org.powerbot.script.internal.randoms.TicketDestroy;
import org.powerbot.script.internal.randoms.WidgetCloser;
import org.powerbot.service.scripts.ScriptDefinition;
import org.powerbot.util.Tracker;

/**
 * @author Timer
 */
public class ScriptHandler {
	public final Logger log = Logger.getLogger(ScriptHandler.class.getName());
	final EventMulticaster eventMulticaster;
	private Script script;
	private ScriptDefinition def;
	private Container scriptContainer, randomContainer;
	private RandomHandler randomHandler;

	public ScriptHandler(final EventMulticaster eventMulticaster) {
		this.eventMulticaster = eventMulticaster;
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
		scriptContainer = script.getContainer();
		scriptContainer.addListener(new JobListener() {
			private boolean stopped = false;

			@Override
			public void jobStarted(final Job job) {
				eventMulticaster.addListener(job);
			}

			@Override
			public void jobStopped(final Job job) {
				eventMulticaster.removeListener(job);

				if (stopped || job.equals(script)) {
					BotChrome.getInstance().toolbar.updateControls();
					stopped = true;
				}
			}
		});

		/* Start the script */
		script.start();

		/* Submit the random handler */
		(randomContainer = new TaskContainer()).submit(randomHandler = new RandomHandler(this, new AntiRandom[]{
				new Login(),
				new BankPin(),
				new TicketDestroy(),
				new WidgetCloser()
		}));
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
		if (randomContainer != null) randomContainer.shutdown();
		if (script != null) {
			script.shutdown();
			track("stop");
		}
	}

	public void stop() {
		if (randomContainer != null) randomContainer.shutdown();
		if (script != null) {
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

	public Container getScriptContainer() {
		return scriptContainer;
	}

	private void track(final String action) {
		if (def == null || def.local || def.getID() == null || def.getID().isEmpty() || def.getName() == null) {
			return;
		}

		final String page = String.format("scripts/%s/%s", def.getID(), action);
		Tracker.getInstance().trackPage(page, def.getName());
	}
}
