package org.powerbot.script.task;

import org.powerbot.script.Script;
import org.powerbot.script.internal.ScriptContainer;

public abstract class PollingTask implements Runnable {
	private final ScriptContainer container;

	public PollingTask(Script script) {
		this(script.getContainer());
	}

	public PollingTask(ScriptContainer container) {
		this.container = container;
	}

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
				break;
			}
		}
	}

	public ScriptContainer getContainer() {
		return this.container;
	}
}
