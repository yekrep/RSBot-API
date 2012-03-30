package org.powerbot.concurrent;

import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * A simple container capable of deploying tasks within a queued thread environment.
 *
 * @author Timer
 */
public class TaskProcessor implements TaskContainer {
	private final ThreadPoolExecutor executor;

	public TaskProcessor(final ThreadGroup threadGroup) {
		executor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 3, Integer.MAX_VALUE,
				0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>(), new ThreadPool(threadGroup));
	}

	/**
	 * {@inheritDoc}
	 */
	public Future<?> submit(final Task task) {
		return executor.submit(task);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isActive() {
		return executor.getActiveCount() > 0;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isLocked() {
		return executor.isShutdown() && executor.isTerminated();
	}

	/**
	 * {@inheritDoc}
	 */
	public void shutdown() {
		executor.shutdown();
	}

	/**
	 * {@inheritDoc}
	 */
	public void stop() {
		executor.shutdownNow();
	}
}
