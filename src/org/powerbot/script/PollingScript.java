package org.powerbot.script;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.powerbot.script.util.Random;

/**
 * An implementation of {@link AbstractScript} which polls (or "loops")
 * indefinitely at intervals returned by the last run.
 *
 */
@SuppressWarnings("EmptyMethod")
public abstract class PollingScript extends AbstractScript {
	private final AtomicBoolean running;
	private final AtomicLong last, delay;

	/**
	 * Blocks other {@link PollingScript}s which have a lower {@link AbstractScript#priority} value.
	 * Only the head item is considered for comparison.
	 */
	protected static final Queue<Integer> threshold = new ConcurrentLinkedQueue<Integer>();

	/**
	 * The sleep bias for {@link #sleep(long)}.
	 * The absolute sleep value is defined by {@code millis * (1 + ((bias % 100) / 100))}.
	 * By default this value is 50 i.e. +50%.
	 *
	 * @deprecated see {@link org.powerbot.script.util.Condition#wait(java.util.concurrent.Callable)}
	 */
	@Deprecated
	protected final AtomicInteger bias = new AtomicInteger(50);

	/**
	 * Creates an instance of a {@link PollingScript}.
	 */
	public PollingScript() {
		running = new AtomicBoolean(false);
		last = new AtomicLong(0);
		delay = new AtomicLong(0);

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

		final long d = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - last.get());
		if (d < delay.get()) {
			try {
				Thread.sleep(delay.get() - d);
			} catch (final InterruptedException ignored) {
			}
		}

		int t;

		try {
			t = !threshold.isEmpty() && threshold.peek() > priority.get() ? 0 : poll();
		} catch (final Throwable e) {
			e.printStackTrace();
			ctx.controller.stop();
			t = 3000;
		}

		delay.set(t < 0 ? 600 : t);
		last.set(System.nanoTime());

		if (!Thread.interrupted() && !ctx.controller.isStopping()) {
			ctx.controller.getExecutor().offer(this);
			Thread.yield();
		}

		running.set(false);
	}

	/**
	 * Causes the currently executing thread to sleep (temporarily cease
	 * execution) for the specified number of milliseconds.
	 *
	 * @param millis the length of time to sleep in milliseconds
	 * @deprecated see {@link org.powerbot.script.util.Condition#wait(java.util.concurrent.Callable)}
	 */
	@Deprecated
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
