package org.powerbot.script.util;

public interface Prioritizable {

	/**
	 * Retrieves the priority.
	 *
	 * @return an absolute priority level on the integer scale
	 */
	public int getPriority();

	/**
	 * Sets the priority.
	 *
	 * @param priority the new priority level
	 */
	public void setPriority(int priority);
}
