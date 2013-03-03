package org.powerbot.core.script.job;

import java.util.concurrent.Future;

import org.powerbot.game.api.util.Random;

/**
 * The most basic implementation of a {@link Job}.
 *
 * @author Timer
 */
public abstract class Task implements Job {
	Future<?> future;
	private Thread thread;
	private Container container;
	private volatile boolean interrupted;

	public Task() {
		container = null;
		interrupted = false;
	}

	public static void sleep(final long time) {
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void work() {
		interrupted = false;
		thread = Thread.currentThread();
		try {
			execute();
		} catch (final ThreadDeath ignored) {
		} catch (final Throwable e) {
			e.printStackTrace();
		}
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
		return !future.isDone();
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
	public Container getContainer() {
		return container;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setContainer(final Container container) {
		this.container = container;
	}

	@Override
	public Future<?> getFuture() {
		return future;
	}

	@Override
	public void setFuture(final Future<?> future) {
		this.future = future;
	}
}
