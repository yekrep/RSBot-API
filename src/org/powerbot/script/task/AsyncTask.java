package org.powerbot.script.task;

public abstract class AsyncTask implements Task, Runnable {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract void run();

	@Override
	public abstract boolean isValid();

	@Override
	public int getPriority() {
		return 0;
	}
}
