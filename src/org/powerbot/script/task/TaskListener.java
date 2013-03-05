package org.powerbot.script.task;

public interface TaskListener {
	public void taskStarted(Task task);

	public void taskStopped(Task task);
}
