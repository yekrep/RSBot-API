package org.powerbot.nscript.task;

import org.powerbot.nscript.internal.ScriptContainer;

public abstract class PollingTask implements Runnable {
	private ScriptContainer container;

	public PollingTask(ScriptContainer container) {
		this.container = container;
	}

	public abstract int poll();

	@Override
	public final void run() {
		while (!container.isStopping()) {
			int sleep;
			try {
				if (container.isSuspended()) sleep = 600;
				else sleep = poll();
			} catch (Exception e) {
				e.printStackTrace();
				sleep = -1;
			}

			if (sleep > 0) {
				try {
					Thread.sleep(sleep);
				} catch (InterruptedException ignored) {
				}
			} else if (sleep == -1) container.stop();
		}
	}

	public ScriptContainer getContainer() {
		return container;
	}
}
