package org.powerbot.script.task;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A synchronous {@code Task} which can interrupt the following {@code Task}s in the group.
 *
 * @author Paris
 */
public abstract class BlockingTask implements Task, Callable<Boolean> {
	private final AtomicInteger priority;

	public BlockingTask() {
		priority = new AtomicInteger(0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract boolean isValid();

	/**
	 * The body of this {@code Task}
	 *
	 * @return {@code true} to continue executing the next {@code Task}, otherwise {@code false} to interrupt execution
	 */
	@Override
	public abstract Boolean call();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getPriority() {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPriority(final int priority) {
		this.priority.set(priority);
	}
}
