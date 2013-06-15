package org.powerbot.script;

public abstract class PollingScript extends AbstractScript {
	public abstract int poll();

	@Override
	public final void run() {
		while (!getGroup().isStopping()) {
			final int sleep;

			if (getGroup().isSuspended()) {
				sleep = 600;
			} else {
				try {
					sleep = poll();
				} catch (final Throwable t) {
					t.printStackTrace();
					getGroup().stop();
					break;
				}
			}

			try {
				Thread.sleep(Math.max(0, sleep));
			} catch (final InterruptedException ignored) {
			}
		}
	}
}
