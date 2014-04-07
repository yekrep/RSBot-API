package org.powerbot.script;

import java.util.Comparator;
import java.util.NavigableSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * An implementation of {@link AbstractScript} which polls (or "loops") indefinitely.
 */
public abstract class PollingScript<C extends ClientContext> extends AbstractScript<C> implements Runnable {

	/**
	 * The priority of this {@link org.powerbot.script.PollingScript} with respect to others.
	 */
	public final AtomicInteger priority;
	/**
	 * Blocks other {@link org.powerbot.script.PollingScript}s with a lower {@link #priority} value
	 */
	protected static final NavigableSet<PollingScript> threshold = new ConcurrentSkipListSet<PollingScript>(new Comparator<PollingScript>() {
		@Override
		public int compare(final PollingScript o1, final PollingScript o2) {
			return o1.priority.get() - o2.priority.get();
		}
	});

	/**
	 * Creates an instance of a {@link PollingScript}.
	 */
	public PollingScript() {
		priority = new AtomicInteger(0);
		getExecQueue(State.START).add(new Runnable() {
			@Override
			public void run() {
				start();
			}
		});
		getExecQueue(State.START).add(this);
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
	 */
	public abstract void poll();

	@Override
	public final void run() {
		if (threshold.isEmpty() || threshold.first().priority.get() <= priority.get()) {
			try {
				poll();
			} catch (final Throwable e) {
				ctx.controller().stop();
				e.printStackTrace();
			}
		}

		if (!Thread.interrupted() && !ctx.controller().isStopping()) {
			ctx.controller().offer(this);
		}

		try {
			Thread.sleep(60);
		} catch (final InterruptedException ignored) {
			Thread.yield();
		}
	}

	/**
	 * Called on {@link Script.State#START}.
	 * This method can either be overridden or ignored.
	 */
	public void start() {
	}

	/**
	 * Called on {@link Script.State#STOP}.
	 * This method can either be overridden or ignored.
	 */
	public void stop() {
	}

	/**
	 * Called on {@link Script.State#SUSPEND}.
	 * This method can either be overridden or ignored.
	 */
	public void suspend() {
	}

	/**
	 * Called on {@link Script.State#RESUME}.
	 * This method can either be overridden or ignored.
	 */
	public void resume() {
	}
}
