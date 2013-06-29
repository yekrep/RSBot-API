package org.powerbot.script;

import org.powerbot.script.util.Random;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * An implementation of {@link AbstractScript} which polls (or "loops")
 * indefinitely at intervals returned by the last run.
 *
 * @author Paris
 */
public abstract class PollingScript extends AbstractScript {
	private final AtomicBoolean running;

	/**
	 * The sleep bias for {@link #sleep(long)} and {@link #poll()}.
	 * The absolute sleep value is defined by {@code millis * (1 + ((bias % 100) / 100))}.
	 * By default this value is 50 i.e. +50%.
	 */
	protected final AtomicInteger bias;

	/**
	 * Creates an instance of a {@link PollingScript}.
	 */
	public PollingScript() {
		running = new AtomicBoolean(false);
		bias = new AtomicInteger(50);

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
			final int min = (int) millis;
			final int max = min * (1 + ((bias.get() % 100) / 100));
			Thread.sleep(Random.nextInt(min, max));
		} catch (final InterruptedException ignored) {
		}
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
