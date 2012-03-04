package org.powerbot.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * A simple processor capable of deploying tasks within a queued thread environment.
 *
 * @author Timer
 */
public class TaskProcessor implements TaskContainer {
	private final ExecutorService executor;

	public TaskProcessor(final ThreadGroup threadGroup) {
		executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2, new ThreadPool(threadGroup));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void submit(final ContainedTask task) {
		final Future<Object> future = executor.submit(task);
		task.setFuture(future);
	}
}
