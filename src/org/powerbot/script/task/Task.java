package org.powerbot.script.task;

/**
 * The base interface for tasks.
 *
 * @author Paris
 */
public interface Task {

	/**
	 * Determines whether or not this task should execute.
	 *
	 * @return {@code true} if this task should be executed, otherwise {@code false}
	 */
	public boolean isValid();

	/**
	 * Determines the priority of this task. Higher priority tasks will always execute before lower priority tasks in a group.
	 *
	 * @return an absolute priority on the integer scale
	 */
	public int getPriority();
}
