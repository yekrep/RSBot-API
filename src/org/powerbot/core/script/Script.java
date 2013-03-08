package org.powerbot.core.script;

import java.util.Arrays;
import java.util.List;

import org.powerbot.bot.Bot;
import org.powerbot.core.script.job.Container;
import org.powerbot.core.script.job.Job;
import org.powerbot.core.script.job.LoopTask;
import org.powerbot.script.internal.ScriptContainer;
import org.powerbot.script.internal.ScriptListener;
import org.powerbot.script.task.Task;

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

	@Override
	public List<Task> getStartupTasks() {
		return Arrays.asList(
				new Task() {
					@Override
					public void execute() {
						_start();
					}
				}, new Task() {
					@Override
					public void execute() {
						Bot.instance().getScriptContainer().addListener(new ScriptListener() {
							@Override
							public void scriptStarted(final ScriptContainer scriptContainer) {
							}

							@Override
							public void scriptPaused(final ScriptContainer scriptContainer) {
								setPaused(true);
							}

							@Override
							public void scriptResumed(final ScriptContainer scriptContainer) {
								setPaused(false);
							}

							@Override
							public void scriptStopped(final ScriptContainer scriptContainer) {
								stop();
							}
						});
					}
				}
		);
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
