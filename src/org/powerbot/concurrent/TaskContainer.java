package org.powerbot.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TaskContainer implements TaskProcessor {
	private ExecutorService executor = Executors.newCachedThreadPool();

	public void submit(ContainedTask task) {
		Future<Object> future = executor.submit(task);
		task.setFuture(future);
	}
}
