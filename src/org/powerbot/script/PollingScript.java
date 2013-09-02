package org.powerbot.script;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.powerbot.script.internal.ScriptController;
import org.powerbot.script.internal.YieldableTask;
import org.powerbot.script.util.Random;

/**
 * An implementation of {@link AbstractScript} which polls (or "loops")
 * indefinitely at intervals returned by the last run.
 *
 * @author Paris
 */
public abstract class PollingScript extends AbstractScript implements YieldableTask {
	private final AtomicBoolean running;
	private final AtomicBoolean yielding;

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
		yielding = new AtomicBoolean(false);
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
		final ScriptController controller = getController();

		while (!controller.isStopping()) {
			final int sleep;

			if (controller.isSuspended() || controller.getPriority() > getPriority()) {
				yielding.set(true);
				sleep = delay;
			} else {
				yielding.set(false);
				try {
					if (isValid() && controller.isYielding()) {
						sleep = poll();
					} else {
						sleep = delay;
					}
				} catch (final Throwable t) {
					t.printStackTrace();
					controller.stop();
					break;
				}
			}

			sleep(Math.max(0, sleep == -1 ? delay : sleep));
		}

		running.set(false);
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public int getPriority() {
		return Thread.MIN_PRIORITY;
	}

	@Override
	public boolean isYielding() {
		return running.get() && yielding.get();
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
