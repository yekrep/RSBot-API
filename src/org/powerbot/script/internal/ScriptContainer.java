package org.powerbot.script.internal;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.powerbot.event.EventMulticaster;
import org.powerbot.game.api.Manifest;
import org.powerbot.gui.BotChrome;
import org.powerbot.script.Script;
import org.powerbot.script.task.Task;
import org.powerbot.service.scripts.ScriptDefinition;

public class ScriptContainer extends AbstractContainer {
	private final Logger log = Logger.getLogger(Script.class.getName());
	private final EventMulticaster multicaster;
	private final Set<ScriptListener> scriptListeners;
	private Script script;
	private ScriptDefinition definition;
	private boolean paused;

	public ScriptContainer(final EventMulticaster multicaster) {
		this.multicaster = multicaster;
		this.scriptListeners = new HashSet<>();
		this.paused = false;
	}

	public void start(final org.powerbot.core.script.Script script, final ScriptDefinition definition) {
		start(new WrapperScript(script), definition);
		addListener(new ScriptListener() {
			@Override
			public void scriptStarted(final ScriptContainer scriptContainer) {
			}

			@Override
			public void scriptPaused(final ScriptContainer scriptContainer) {
				script.setPaused(true);
			}

			@Override
			public void scriptResumed(final ScriptContainer scriptContainer) {
				script.setPaused(false);
			}

			@Override
			public void scriptStopped(final ScriptContainer scriptContainer) {
				script.stop();
			}
		});
	}

	@Override
	public boolean isActive() {
		if (script == null) return false;
		return super.isActive();
	}

	public void start(final Script script, final ScriptDefinition definition) {
		this.script = script;
		this.definition = definition;
		addListener(new ScriptListener() {
			@Override
			public void scriptStarted(final ScriptContainer scriptContainer) {
				BotChrome.getInstance().toolbar.updateControls();
			}

			@Override
			public void scriptPaused(final ScriptContainer scriptContainer) {
				BotChrome.getInstance().toolbar.updateControls();
			}

			@Override
			public void scriptResumed(final ScriptContainer scriptContainer) {
				BotChrome.getInstance().toolbar.updateControls();
			}

			@Override
			public void scriptStopped(final ScriptContainer scriptContainer) {
				BotChrome.getInstance().toolbar.updateControls();
			}
		});

		log.info("Starting script: " + definition.getName() + " by " + definition.getAuthors());
		script.start();
		final List<Task> list = script.getStartupTasks();
		final Iterator<Task> iterator = list.iterator();
		while (iterator.hasNext()) submit(iterator.next());
		final Iterator<ScriptListener> iterator2 = scriptListeners.iterator();
		while (iterator2.hasNext()) iterator2.next().scriptStarted(this);
	}

	public void addListener(final ScriptListener listener) {
		this.scriptListeners.add(listener);
	}

	public void removeListener(final ScriptListener listener) {
		this.scriptListeners.remove(listener);
	}

	@Override
	public void stop() {
		if (!isStopped()) {
			log.info("Stopping script: " + definition.getName());
			super.stop();
			final Iterator<ScriptListener> iterator = scriptListeners.iterator();
			while (iterator.hasNext()) iterator.next().scriptStopped(this);
			//TODO ensure stop (new thread + move listener here)
		}
	}

	@Override
	public boolean isPaused() {
		return paused;
	}

	public void setPaused(final boolean paused) {
		if (this.paused != paused) {
			this.paused = paused;
			final Iterator<ScriptListener> iterator = scriptListeners.iterator();
			while (iterator.hasNext()) {
				final ScriptListener l = iterator.next();
				if (paused) l.scriptPaused(this);
				else l.scriptResumed(this);
			}
		}
	}

	@Override
	public void taskStarted(final Task task) {
		super.taskStarted(task);
		multicaster.addListener(task);
	}

	@Override
	public void taskStopped(final Task task) {
		super.taskStopped(task);
		multicaster.removeListener(task);
		if (task == this.script) stop();
	}

	public Script getScript() {
		return this.script;
	}

	public ScriptDefinition getDefinition() {
		return this.definition;
	}

	@Manifest(name = "backwards-compatibility wrapper", description = "this script it outdated", authors = {"internals - this script is OUTDATED"}, singleinstance = true)
	private final class WrapperScript implements Script {
		private final org.powerbot.core.script.Script script;

		private WrapperScript(org.powerbot.core.script.Script script) {
			this.script = script;
		}

		@Override
		public void start() {
		}

		@Override
		public boolean isActive() {
			return script.isActive();
		}

		@Override
		public boolean isPaused() {
			return script.isPaused();
		}

		@Override
		public List<Task> getStartupTasks() {
			return Arrays.asList(new Task[]{new Task() {
				@Override
				public void execute() {
					script.start();
				}
			}});
		}
	}
}
