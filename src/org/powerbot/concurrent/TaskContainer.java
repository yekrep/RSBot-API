package org.powerbot.concurrent;

/**
 * A processor in which is able to perform basic, mandatory invocations in regards to tasks.
 *
 * @author Timer
 */
public interface TaskContainer {
	/**
	 * Submits a task for processing and execution.
	 *
	 * @param task The task to be deployed.
	 */
	public void submit(Task task);

	/**
	 * Shuts down this container from accepting any more tasks.
	 */
	public void shutdown();

	/**
	 * Stops this container entirely.
	 */
	public void stop();
}
