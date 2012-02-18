package org.powerbot.concurrent;

import java.util.concurrent.Future;

/**
 * A containable task that can be managed and records its Future.
 *
 * @author Timer
 */
public abstract class ContainedTask implements Task {
	public Future<Object> future = null;

	/**
	 * Sets the future associated with the last invoke of this task.
	 *
	 * @param future The future associated with this task.
	 */
	public void setFuture(Future<Object> future) {
		this.future = future;
	}
}
