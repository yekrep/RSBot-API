package org.powerbot.script;

import java.util.Queue;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;

import org.powerbot.script.framework.Stoppable;
import org.powerbot.script.framework.Suspendable;

/**
 * An implementation of {@code Script} which polls (or "loops") indefinitely.
 *
 * @author Paris
 */
public abstract class PollingScript extends AbstractScript implements Suspendable, Stoppable {
	private final AtomicBoolean suspended, stopping;

	public PollingScript() {
		suspended = new AtomicBoolean(false);
		stopping = new AtomicBoolean(false);

		getTasks(State.SUSPEND).add(new FutureTask<>(new Runnable() {
			@Override
			public void run() {
				suspended.set(true);
				suspend();
			}
		}, true));

		getTasks(State.RESUME).add(new FutureTask<>(new Runnable() {
			@Override
			public void run() {
				suspended.set(false);
				resume();
			}
		}, true));

		getTasks(State.STOP).add(new FutureTask<>(new Runnable() {
			@Override
			public void run() {
				stopping.set(true);
				stop();
			}
		}, true));
	}

	/**
	 * The body of the loop.
	 *
	 * @return the delay in milliseconds before the next call
	 */
	public abstract int poll();

	/**
	 * Initiates this {@code Script}.
	 */
	@Override
	public final void run() {
		start();
		getScriptController().getExecutorService().submit(new Runnable() {
			@Override
			public void run() {
				while (!stopping.get()) {
					final Queue<Script> queue = getScriptController().getLockQueue();
					sleep(Math.max(0, suspended.get() || (!queue.isEmpty() && !queue.contains(PollingScript.this)) ? 600 : poll()));
				}
			}
		});
	}

	/**
	 * Called when the script is first started.
	 */
	public void start() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isSuspended() {
		return suspended.get();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void suspend() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void resume() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isStopping() {
		return stopping.get();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void stop() {
	}
}
