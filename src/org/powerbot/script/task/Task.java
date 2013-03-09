package org.powerbot.script.task;

import org.powerbot.script.Prioritizable;

/**
 * The base interface for tasks.
 *
 * @author Paris
 */
public interface Task extends Prioritizable {

	/**
	 * Determines whether or not this task should execute.
	 *
	 * @return {@code true} if this task should be executed, otherwise {@code false}
	 */
	public boolean isValid();
}
