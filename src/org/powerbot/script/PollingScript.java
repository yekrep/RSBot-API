package org.powerbot.script;

public abstract class PollingScript extends AbstractScript {

	public abstract int poll();

	@Override
	public final void run() {
		final int delay = 600;

		while (!getController().isStopping()) {
			final int sleep;

			if (getController().isSuspended()) {
				sleep = delay;
			} else {
				try {
					sleep = poll();
				} catch (final Throwable t) {
					t.printStackTrace();
					getController().stop();
					break;
				}
			}

			sleep(Math.max(0, sleep == -1 ? delay : sleep));
		}
	}

	/**
	 * Causes the currently executing thread to sleep (temporarily cease
	 * execution) for the specified number of milliseconds.
	 *
	 * @param millis the length of time to sleep in milliseconds
	 */
	public final void sleep(final long millis) {
		try {
			Thread.sleep(millis);
		} catch (final InterruptedException ignored) {
		}
	}
}
