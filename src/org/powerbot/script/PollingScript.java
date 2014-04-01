package org.powerbot.script;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * An implementation of {@link AbstractScript} which polls (or "loops") indefinitely.
 */
public abstract class PollingScript<C extends ClientContext> extends AbstractScript<C> implements Runnable {

	/**
	 * Blocks other {@link PollingScript}s which have a lower {@link AbstractScript#priority} value.
	 * Only the head item is considered for comparison.
	 */
	protected static final Queue<Integer> threshold = new ConcurrentLinkedQueue<Integer>();

	/**
	 * Creates an instance of a {@link PollingScript}.
	 */
	public PollingScript() {
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
		if (threshold.isEmpty() || priority.get() <= threshold.peek()) {
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

		Thread.yield();
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
