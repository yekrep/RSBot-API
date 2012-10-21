package org.powerbot.core.script.job;

/**
 * A {@link org.powerbot.core.script.job.Job} which loops.
 * <p/>
 * Returning the value of <code>-1</code> will cause this {@link LoopTask} to shutdown.
 * This {@link LoopTask} will also come to a stop if its {@link org.powerbot.core.script.job.Container} is shutdown.
 * <p/>
 * Pausing a {@link LoopTask} will cause suspension of the {@link Thread} for iterations of 1000 milliseconds until resumed.
 *
 * @author Timer
 */
public abstract class LoopTask extends Task {
	private boolean paused;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void execute() {
		while (!getContainer().isShutdown()) {
			if (getContainer().isPaused()) {
				paused = true;
				Task.sleep(1000);
				continue;
			}
			paused = false;

			int time;
			try {
				time = loop();
			} catch (final Throwable e) {
				e.printStackTrace();
				time = -1;
			}

			if (time >= 0) {
				Task.sleep(time);
			} else if (time == -1) {
				break;
			}
		}
	}

	/**
	 * A method which has the intended effect of <b>one</b> 'tick' or 'action' per iteration.
	 * Returning <code>-1</code> will cause this {@link LoopTask} to stop.
	 *
	 * @return The amount of time to sleep before the next iteration.
	 */
	public abstract int loop();

	public boolean isPaused() {
		return paused;
	}
}
