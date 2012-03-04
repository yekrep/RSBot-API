package org.powerbot.concurrent.action;

import org.powerbot.concurrent.ContainedTask;

/**
 * A container for [a] task[s] to be performed through.
 *
 * @author Timer
 */
public class ActionComposite {
	public ContainedTask[] tasks;

	/**
	 * Initializes this <code>ActionComposite</code> for response to Action activations.
	 *
	 * @param tasks The <code>ContainedTask</code> associated with this <code>ActionComposite</code>.
	 */
	public ActionComposite(ContainedTask... tasks) {
		this.tasks = tasks;
	}
}
