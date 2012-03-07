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
	public Object call() throws Exception {
		run();
		return null;
	}

	/**
	 * Creates a <code>RunnableTask</code> from the given <code>Runnable</code>.
	 *
	 * @param runnable The <code>Runnable</code> to create this task with.
	 * @return The <code>RunnableTask</code> associated with the given <code>Runnable</code>.
	 */
	public static ContainedTask create(final Runnable runnable) {
		return new RunnableTask() {
			public void run() {
				runnable.run();
			}
		};
	}
}
