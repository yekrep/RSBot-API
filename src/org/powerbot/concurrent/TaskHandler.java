package org.powerbot.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * A simple handler capable of deploying tasks within a queued thread environment.
 *
 * @author Timer
 */
public class TaskHandler implements TaskContainer {
	private ExecutorService executor;

	public TaskHandler(ThreadGroup threadGroup) {
		this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2, new ThreadPool(threadGroup));
	}

	/**
	 * {@inheritDoc}
	 */
	public void submit(ContainedTask task) {
		Future<Object> future = executor.submit(task);
		task.setFuture(future);
	}
}
