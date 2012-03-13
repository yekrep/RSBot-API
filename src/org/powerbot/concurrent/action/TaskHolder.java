package org.powerbot.concurrent.action;

import org.powerbot.concurrent.Task;

/**
 * A container for [a] task[s] to be performed through.
 *
 * @author Timer
 */
public class TaskHolder {
	public Task[] tasks;

	/**
	 * Initializes this <code>ActionComposite</code> for response to Action activations.
	 *
	 * @param tasks The <code>ContainedTask</code> associated with this <code>ActionComposite</code>.
	 */
	public TaskHolder(final Task... tasks) {
		this.tasks = tasks;
	}
}
