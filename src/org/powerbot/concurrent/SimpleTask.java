package org.powerbot.concurrent;

/**
 * A simple task that only needs to be executed.
 *
 * @author Timer
 */
public abstract class SimpleTask extends ContainedTask implements Runnable {
	/**
	 * {@inheritDoc}
	 */
	public Object call() throws Exception {
		run();
		return null;
	}
}
