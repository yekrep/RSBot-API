package org.powerbot.script;

import java.util.concurrent.atomic.AtomicBoolean;

import org.powerbot.script.util.Random;

public abstract class PollingScript extends AbstractScript {
	private final AtomicBoolean running;

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

	public abstract int poll();

	@Override
	public final void run() {
		if (!running.compareAndSet(false, true)) {
			return;
		}

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
	 * Called on {@link State#START}.
	 * This method can either be overriden or ignored.
	 */
	public void start() {
	}

	/**
	 * Called on {@link State#STOP}.
	 * This method can either be overriden or ignored.
	 */
	public void stop() {
	}

	/**
	 * Called on {@link State#SUSPEND}.
	 * This method can either be overriden or ignored.
	 */
	public void suspend() {
	}

	/**
	 * Called on {@link State#RESUME}.
	 * This method can either be overriden or ignored.
	 */
	public void resume() {
	}
}
