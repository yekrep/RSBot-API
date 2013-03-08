package org.powerbot.core.script;

import java.util.Arrays;
import java.util.List;

import org.powerbot.core.script.job.Container;
import org.powerbot.core.script.job.Job;
import org.powerbot.core.script.job.LoopTask;
import org.powerbot.core.script.job.Task;

/**
 * @author Timer
 */
@Deprecated
public abstract class Script extends LoopTask implements org.powerbot.script.Script {
	/**
	 * @return {@link Job}s to be ran upon invocation of {@link org.powerbot.core.script.Script#_start()}.
	 */
	public abstract List<Job> getStartupJobs();

	/**
	 * Starts this {@link Script}'s execution.
	 */
	public abstract void _start();

	/**
	 * @return Whether or not this {@link Script} is active.
	 */
	public abstract boolean isActive();

	/**
	 * @return Whether or not this {@link Script} is paused.
	 */
	public abstract boolean isPaused();

	/**
	 * Sets the pause state of this script.
	 *
	 * @param paused <tt>true</tt> to be paused, <tt>false</tt> to be resumed.
	 */
	public abstract void setPaused(final boolean paused);

	public final void start() {
	}

	public List<Task> getStartupTasks() {
		return Arrays.asList(new Task[] {
				new Task() {
					@Override
					public void execute() {
						_start();
					}
				}
		});
	}

	/**
	 * Shuts down this {@link Script} for a graceful stop.
	 */
	public abstract void shutdown();

	/**
	 * @return Whether or not this {@link Script} is shutdown.
	 */
	public abstract boolean isShutdown();

	/**
	 * Stops this {@link Script} ungracefully.
	 */
	public abstract void stop();

	/**
	 * @return The parent {@link Container} of this {@link Script}.
	 */
	public abstract Container getContainer();
}
