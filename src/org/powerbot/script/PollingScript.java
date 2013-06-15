package org.powerbot.script;

public abstract class PollingScript extends AbstractScript {
	public abstract int poll();

	@Override
	public final void run() {
		while (!getContainer().isStopping()) {
			int sleep;
			try {
				if (getContainer().isSuspended()) {
					sleep = 600;
				} else {
					sleep = poll();
				}
			} catch (Throwable e) {
				e.printStackTrace();
				sleep = -1;
			}

			if (sleep > 0) {
				try {
					Thread.sleep(sleep);
				} catch (InterruptedException ignored) {
				}
			} else if (sleep == -1) {
				getContainer().stop();
			}
		}
	}
}
