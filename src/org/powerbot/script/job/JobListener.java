package org.powerbot.script.job;

/**
 * A listener interface for manipulation around {@link Job}s.
 *
 * @author Timer
 */
public interface JobListener {
	/**
	 * @param job The {@link Job} that was started.
	 */
	public void jobStarted(final Job job);

	/**
	 * @param job The {@link Job} that was stopped.
	 */
	public void jobStopped(final Job job);
}
