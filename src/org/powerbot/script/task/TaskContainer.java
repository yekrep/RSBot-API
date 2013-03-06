package org.powerbot.script.task;

public interface TaskContainer {
	public boolean submit(Task paramTask);

	public void stop();

	public boolean isActive();

	public boolean isPaused();

	public boolean isStopped();
}
