package org.powerbot.script;

import java.util.Comparator;
import java.util.NavigableSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * An implementation of {@link AbstractScript} which polls (or "loops") indefinitely.
 *
 * @param <C> the type of client
 */
public abstract class PollingScript<C extends ClientContext> extends AbstractScript<C> {
	/**
	 * The priority of this {@link org.powerbot.script.PollingScript} with respect to others.
	 */
	public final AtomicInteger priority;
	/**
	 * Blocks other {@link org.powerbot.script.PollingScript}s with a lower {@link #priority} value.
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
				try {
					start();
				} catch (final Throwable e) {
					ctx.controller.stop();
					final Thread.UncaughtExceptionHandler x = Thread.getDefaultUncaughtExceptionHandler();
					if (x != null) {
						x.uncaughtException(Thread.currentThread(), e);
					} else {
						e.printStackTrace();
					}
				}
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

		getExecQueue(State.START).add(new Runnable() {
			@Override
			public void run() {
				if (threshold.isEmpty() || threshold.first().priority.get() <= priority.get()) {
					try {
						poll();
					} catch (final Throwable e) {
						ctx.controller.stop();
						final Thread.UncaughtExceptionHandler x = Thread.getDefaultUncaughtExceptionHandler();
						if (x != null) {
							x.uncaughtException(Thread.currentThread(), e);
						} else {
							e.printStackTrace();
						}
					}
				}

				try {
					Thread.sleep(60);
				} catch (final InterruptedException ignored) {
					Thread.yield();
				}

				if (!Thread.interrupted() && !ctx.controller.isStopping()) {
					ctx.controller.offer(this);
				}
			}
		});
	}

	/**
	 * The main body of this {@link PollingScript}, which is called in a single-threaded loop.
	 */
	public abstract void poll();

	/**
	 * Called on {@link Script.State#START}.
	 */
	public void start() {
	}

	/**
	 * Called on {@link Script.State#STOP}.
	 */
	public void stop() {
	}

	/**
	 * Called on {@link Script.State#SUSPEND}.
	 */
	public void suspend() {
	}

	/**
	 * Called on {@link Script.State#RESUME}.
	 */
	public void resume() {
	}
}
