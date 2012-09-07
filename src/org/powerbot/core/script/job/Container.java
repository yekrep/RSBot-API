package org.powerbot.core.script.job;

/**
 * A controllable application interface used for containing a number of {@link Job}s.
 *
 * @author Timer
 */
public interface Container {
	/**
	 * Submits a worker to this container to begin its job.
	 *
	 * @param job The {@link Job} to execute.
	 */
	public void submit(Job job);

	/**
	 * Pauses this container for {@link Job}s which obey its rule.
	 *
	 * @param paused <tt>true</tt> to pause; otherwise, <tt>false</tt>.
	 */
	public void setPaused(final boolean paused);

	/**
	 * @return Whether or not this {@link Container} is paused.
	 */
	public boolean isPaused();

	/**
	 * Enumerates an array containing all living instances of {@link Job}.
	 *
	 * @return The {@link Job}[] containing living jobs within this container.
	 */
	public Job[] enumerate();

	/**
	 * @return Evaluates the number of living instances of {@link Job} within this container.
	 */
	public int getActiveCount();

	/**
	 * Branches this {@link Container} into a child.
	 *
	 * @return The {@link Container} child of this {@link Container}.
	 */
	public Container branch();

	/**
	 * @return The {@link Container}[] of children belonging to this {@link Container}.
	 */
	public Container[] getChildren();

	/**
	 * Shuts down this container.
	 * <p/>
	 * Rejects any future submitted {@link Job} to this container.
	 */
	public void shutdown();

	/**
	 * @return Whether or not this {@link Container} is shutdown.
	 */
	public boolean isShutdown();

	/**
	 * Interrupts this {@link Container}, and all of its predecessors (children).
	 */
	public void interrupt();

	/**
	 * @return <tt>true</tt> if terminated; otherwise, <tt>false</tt>.
	 */
	public boolean isTerminated();

	/**
	 * @param listener The {@link JobListener} to be notified.
	 */
	public void addListener(final JobListener listener);

	/**
	 * @param listener The {@link JobListener} to no longer be notified.
	 */
	public void removeListener(final JobListener listener);
}
