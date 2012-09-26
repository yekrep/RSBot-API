package org.powerbot.core.script.job;

import java.util.EventListener;

/**
 * A {@link Job} is generally deployed in an asynchronous or concurrent environment.
 * Submitted to a {@link Container}.
 *
 * @author Timer
 */
public interface Job extends EventListener {
	/**
	 * Executes the job or task of this worker.
	 */
	public void work();

	/**
	 * Makes an attempt to wait for this thread to complete by pausing (suspending) the calling thread.
	 *
	 * @return <tt>true</tt> when successfully joined; otherwise <tt>false</tt>.
	 */
	public boolean join();

	/**
	 * @return Whether or not this worker is alive.
	 */
	public boolean isAlive();

	/**
	 * Interrupts the working, bringing it to a halt (if alive).
	 */
	public void interrupt();

	/**
	 * @return Whether or not this worker has been interrupted.
	 */
	public boolean isInterrupted();

	/**
	 * Sets the {@link Container} of which this {@link Job} was submitted to.
	 * Internal use only.
	 *
	 * @param container The {@link Container} in which this {@link Job} was submitted to.
	 */
	public void setContainer(final Container container);

	/**
	 * @return The {@link Container} in which this {@link Job} was submitted to.
	 */
	public Container getContainer();
}
