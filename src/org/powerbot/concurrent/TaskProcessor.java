package org.powerbot.concurrent;

public interface TaskProcessor {
	public void submit(ContainedTask task);
}
