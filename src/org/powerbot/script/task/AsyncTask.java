package org.powerbot.script.task;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * An asynchronous {@code Task}.
 *
 * @author Paris
 */
public abstract class AsyncTask implements Task, Runnable {
	private final AtomicInteger priority;

	public AsyncTask() {
		priority = new AtomicInteger(0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract void run();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract boolean isValid();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getPriority() {
		return priority.get();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPriority(final int priority) {
		this.priority.set(priority);
	}
}
