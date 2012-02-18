package org.powerbot.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * A simple container capable of deploying tasks within a cached thread environment.
 *
 * @author Timer
 */
public class TaskContainer implements TaskProcessor {
	private ExecutorService executor = Executors.newCachedThreadPool();

	/**
	 * {@inheritDoc}
	 */
	public void submit(ContainedTask task) {
		Future<Object> future = executor.submit(task);
		task.setFuture(future);
	}
}
