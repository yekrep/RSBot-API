package org.powerbot.concurrent;

/**
 * A simple task that only needs to be executed.
 *
 * @author Timer
 */
public abstract class RunnableTask extends ContainedTask implements Runnable {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object call() throws Exception {
		run();
		return null;
	}

	public static ContainedTask create(final Runnable runnable) {
		return new RunnableTask() {
			@Override
			public void run() {
				runnable.run();
			}
		};
	}
}
