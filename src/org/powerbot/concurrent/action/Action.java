package org.powerbot.concurrent.action;

import java.util.concurrent.Future;

import org.powerbot.concurrent.Task;
import org.powerbot.lang.Activatable;

/**
 * An action that is performed when it can be activated.
 *
 * @author Timer
 */
public class Action {
	public Activatable activator;
	public Task[] tasks;

	public boolean requireLock;
	public boolean resetExecutionQueue;
	public boolean synchronizeInstances;
	public Future<?>[] futures;

	/**
	 * Initializes this <code>Action</code> with appropriate information required for processing.
	 *
	 * @param activator The <code>Activatable</code> associated with this <code>Action</code>.
	 * @param tasks     The tasks associated with this <code>Action</code>.
	 */
	public Action(final Activatable activator, final Task... tasks) {
		this.activator = activator;
		this.tasks = tasks;

		requireLock = true;
		resetExecutionQueue = false;
		synchronizeInstances = true;
		futures = null;
	}

	boolean isIdle() {
		if (futures != null) {
			for (final Future<?> future : futures) {
				if (!future.isDone()) {
					return false;
				}
			}
		}
		return true;
	}
}
