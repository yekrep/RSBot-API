package org.powerbot.script;

public abstract class PollingScript extends AbstractScript {
	public static final int DEFAULT_DELAY = 600;

	public abstract int poll();

	@Override
	public final void run() {
		while (!getController().isStopping()) {
			final int sleep;

			if (getController().isSuspended()) {
				sleep = DEFAULT_DELAY;
			} else {
				try {
					sleep = poll();
				} catch (final Throwable t) {
					t.printStackTrace();
					getController().stop();
					break;
				}
			}

			sleep(Math.max(0, sleep == -1 ? DEFAULT_DELAY : sleep));
		}
	}

	public final void sleep(final long millis) {
		try {
			Thread.sleep(millis);
		} catch (final InterruptedException ignored) {
		}
	}
}
