package org.powerbot.script;

import org.powerbot.script.util.Random;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * An implementation of {@link AbstractScript} which polls (or "loops")
 * indefinitely at intervals returned by the last run.
 *
 * @author Paris
 */
public abstract class PollingScript extends AbstractScript {
	private final AtomicBoolean running;

	/**
	 * Creates an instance of a {@link PollingScript}.
	 */
	public PollingScript() {
		running = new AtomicBoolean(false);

		getExecQueue(State.START).add(new Runnable() {
			@Override
			public void run() {
				start();
			}
		});
		getExecQueue(State.STOP).add(new Runnable() {
			@Override
			public void run() {
				stop();
			}
		});
		getExecQueue(State.SUSPEND).add(new Runnable() {
			@Override
			public void run() {
				suspend();
			}
		});
		getExecQueue(State.RESUME).add(new Runnable() {
			@Override
			public void run() {
				resume();
			}
		});
	}

	/**
	 * The main body of this {@link PollingScript}, which is called in a single-threaded loop.
	 *
	 * @return the delay in milliseconds before calling this method again
	 */
	public abstract int poll();

	@Override
	public final void run() {
		if (!running.compareAndSet(false, true)) {
			return;
		}

		final int delay = 600;

		while (!getController().isStopping()) {
			final int sleep;

			if (getController().isSuspended() || getController().getPriority() > getPriority()) {
				sleep = delay;
			} else {
				try {
					if (isValid()) {
						sleep = poll();
					} else sleep = delay;
				} catch (final Throwable t) {
					t.printStackTrace();
					getController().stop();
					break;
				}
			}

			sleep(Math.max(0, sleep == -1 ? delay : sleep));
		}

		running.set(false);
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

	/**
	 * Causes the currently executing thread to sleep (temporarily cease
	 * execution) for a random number of milliseconds within the specified bounds.
	 *
	 * @param min the inclusive lower bound
	 * @param max the exclusive upper bound
	 */
	public final void sleep(final int min, final int max) {
		sleep(Random.nextInt(min, max));
	}

	/**
	 * Called on {@link org.powerbot.script.Script.State#START}.
	 * This method can either be overridden or ignored.
	 */
	public void start() {
	}

	/**
	 * Called on {@link org.powerbot.script.Script.State#STOP}.
	 * This method can either be overridden or ignored.
	 */
	public void stop() {
	}

	/**
	 * Called on {@link org.powerbot.script.Script.State#SUSPEND}.
	 * This method can either be overridden or ignored.
	 */
	public void suspend() {
	}

	/**
	 * Called on {@link org.powerbot.script.Script.State#RESUME}.
	 * This method can either be overridden or ignored.
	 */
	public void resume() {
	}
}
