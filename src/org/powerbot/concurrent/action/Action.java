package org.powerbot.concurrent.action;

import org.powerbot.concurrent.Task;
import org.powerbot.lang.Activatable;

/**
 * An action that is performed when it can be activated.
 *
 * @author Timer
 */
public class Action {
	public boolean requireLock;
	public boolean resetExecutionQueue;
	public Activatable activator;
	public Task[] tasks;

	/**
	 * Initializes this <code>Action</code> with appropriate information required for processing.
	 *
	 * @param activator The <code>Activator</code> associated with this <code>Action</code>.
	 * @param tasks     The tasks associated with this <code>Action</code>.
	 */
	public Action(final Activatable activator, final Task... tasks) {
		this.activator = activator;
		this.tasks = tasks;
		requireLock = true;
		resetExecutionQueue = false;
	}
}
