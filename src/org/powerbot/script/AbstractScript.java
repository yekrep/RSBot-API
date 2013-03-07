package org.powerbot.script;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.powerbot.script.task.LoopTask;
import org.powerbot.script.task.Task;

public abstract class AbstractScript extends LoopTask implements Script {
	protected final Logger log = Logger.getLogger(getClass().getName());
	protected final List<Task> startupTasks = new LinkedList<>();

	@Override
	public void start() {
		if (!this.startupTasks.contains(this)) this.startupTasks.add(this);
	}

	@Override
	public boolean isPaused() {
		return getContainer().isPaused();
	}

	@Override
	public List<Task> getStartupTasks() {
		return this.startupTasks;
	}

	@Override
	public boolean onStart() {
		return true;
	}

	@Override
	public void onFinish() {
		this.startupTasks.clear();
	}
}
