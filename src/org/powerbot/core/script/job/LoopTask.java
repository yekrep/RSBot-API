package org.powerbot.core.script.job;

import org.powerbot.core.concurrent.Task;

/**
 * A {@link org.powerbot.core.concurrent.Job} in which loops until interrupted or shutdown (return value -1).
 *
 * @author Timer
 */
public abstract class LoopTask extends Task {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void execute() {
		while (!getContainer().isShutdown()) {
			if (getContainer().isPaused()) {
				Task.sleep(1000);
				continue;
			}

			int time;
			try {
				time = loop();
			} catch (final Throwable ignored) {
				time = -1;
			}

			if (time > 0) {
				Task.sleep(time);
			} else if (time == -1) {
				break;
			}
		}
	}

	/**
	 * A method which has the intended effect of <b>one</b> 'tick' or 'action' per iteration.
	 *
	 * @return The amount of time to sleep before the next iteration.
	 */
	public abstract int loop();
}
