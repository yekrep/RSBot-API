package org.powerbot.concurrent;

import java.util.concurrent.Future;

/**
 * A representation of a task submittable to a container for propagation, caching, and execution.
 * Records provided <code>Future</code>.
 *
 * @author Timer
 */
public abstract class Task implements Runnable {
	public Future<?> future = null;

	/**
	 * Sets the <code>Future</code> associated with the last invoke of this task.
	 *
	 * @param future The <code>Future</code> associated with this task.
	 */
	public void setFuture(final Future<?> future) {
		this.future = future;
	}
}
