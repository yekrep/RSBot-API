package org.powerbot.script.internal;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.powerbot.script.Script;
import org.powerbot.script.task.Task;

public class ScriptContainer extends AbstractContainer {
	private final Set<ScriptListener> scriptListeners;
	private Script script;
	private boolean paused;

	public ScriptContainer() {
		this.scriptListeners = new HashSet<>();
		this.paused = false;
	}

	public void start(final Script script) {
		this.script = script;
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
	public void taskStopped(final Task task) {
		super.taskStopped(task);
		if (task == this.script) stop();
	}
}
