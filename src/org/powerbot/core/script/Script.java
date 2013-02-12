package org.powerbot.core.script;

import java.util.List;

import org.powerbot.core.script.job.Container;
import org.powerbot.core.script.job.Job;

/**
 * @author Timer
 */
public interface Script {
	/**
	 * @return {@link Job}s to be ran upon invocation of {@link org.powerbot.core.script.Script#start()}.
	 */
	public List<Job> getStartupJobs();

	/**
	 * Starts this {@link Script}'s execution.
	 */
	public void start();

	/**
	 * @return Whether or not this {@link Script} is active.
	 */
	public boolean isActive();

	/**
	 * @return Whether or not this {@link Script} is paused.
	 */
	public boolean isPaused();

	/**
	 * Sets the pause state of this script.
	 *
	 * @param paused <tt>true</tt> to be paused, <tt>false</tt> to be resumed.
	 */
	public void setPaused(final boolean paused);

	/**
	 * Shuts down this {@link Script} for a graceful stop.
	 */
	public void shutdown();

	/**
	 * @return Whether or not this {@link Script} is shutdown.
	 */
	public boolean isShutdown();

	/**
	 * Stops this {@link Script} ungracefully.
	 */
	public void stop();

	/**
	 * @return The parent {@link Container} of this {@link Script}.
	 */
	public Container getContainer();
}
