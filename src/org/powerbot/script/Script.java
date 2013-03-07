package org.powerbot.script;

import java.util.EventListener;
import java.util.List;

import org.powerbot.script.task.Task;

public interface Script extends EventListener {
	public void start();

	public boolean isActive();

	public boolean isPaused();

	public List<Task> getStartupTasks();
}
