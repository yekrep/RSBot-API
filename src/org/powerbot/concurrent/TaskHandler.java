package org.powerbot.concurrent;

import java.util.concurrent.*;

/**
 * A simple container capable of deploying tasks within a cached thread environment.
 *
 * @author Timer
 */
public class TaskHandler implements TaskContainer {
	private ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

	/**
	 * {@inheritDoc}
	 */
	public void submit(ContainedTask task) {
		Future<Object> future = executor.submit(task);
		task.setFuture(future);
	}
}
