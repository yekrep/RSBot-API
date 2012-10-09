package org.powerbot.core.script.job;

import java.util.concurrent.Future;

import org.powerbot.game.api.util.Random;

/**
 * The most basic implementation of a {@link Job}.
 *
 * @author Timer
 */
public abstract class Task implements Job {
	private Thread thread;
	Future<?> future;
	private Container container;
	private volatile boolean alive, interrupted;
	private final Object init_lock;

	public Task() {
		container = null;
		alive = false;
		interrupted = false;
		init_lock = new Object();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void work() {
		synchronized (init_lock) {
			if (alive) {
				return;//TODO duplicate task submission
			}
			alive = true;
		}

		interrupted = false;
		thread = Thread.currentThread();
		try {
			execute();
		} catch (final ThreadDeath ignored) {
		} catch (final Throwable e) {
			//TODO uncaught exception
			e.printStackTrace();
		}
		alive = false;
	}

	/**
	 * The task to execute.
	 */
	public abstract void execute();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean join() {
		if (future == null || future.isDone()) {
			return true;
		}
		try {
			future.get();
		} catch (final Throwable ignored) {
		}
		return future.isDone();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isAlive() {
		return alive;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void interrupt() {
		interrupted = true;

		if (thread != null) {
			try {
				if (!thread.isInterrupted()) {
					thread.interrupt();
				}
			} catch (final Throwable ignored) {
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isInterrupted() {
		return interrupted;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setContainer(final Container container) {
		this.container = container;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Container getContainer() {
		return container;
	}

	public static void sleep(final int time) {
		if (Thread.currentThread().isInterrupted()) {
			throw new ThreadDeath();
		}

		if (time > 0) {
			try {
				Thread.sleep(time);
			} catch (final InterruptedException ignored) {
				throw new ThreadDeath();
			}
		}
	}

	public static void sleep(final int min, final int max) {
		sleep(Random.nextInt(min, max));
	}
}
