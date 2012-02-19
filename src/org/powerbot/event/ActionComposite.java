package org.powerbot.event;

import org.powerbot.concurrent.ContainedTask;

/**
 * A container for a task to be performed through.
 *
 * @author Timer
 */
public interface ActionComposite {
	/**
	 * Returns the task that is performed when this <code>ActionComposite</code> is executed.
	 *
	 * @return The <code>ContainedTask</code> associated with this <code>ActionComposite</code>.
	 */
	public ContainedTask createTask();
}
