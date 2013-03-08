package org.powerbot.script.task;

/**
 * An asynchronous {@code Task}.
 *
 * @author Paris
 */
public abstract class AsyncTask implements Task, Runnable {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract void run();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract boolean isValid();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getPriority() {
		return 0;
	}
}
