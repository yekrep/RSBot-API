package org.powerbot.script.job;

/**
 * The most basic implementation of a {@link Job}.
 *
 * @author Timer
 */
public abstract class Task implements Job {
	private Thread current_thread;
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
		current_thread = Thread.currentThread();
		try {
			execute();
		} catch (final ThreadDeath ignored) {
		} catch (final Throwable ignored) {
			//TODO uncaught
			ignored.printStackTrace();
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
	public final void join() {
		join(0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean join(final int timeout) {
		if (!alive || current_thread == null) {
			return true;
		}
		try {
			current_thread.join(timeout);
			return !current_thread.isAlive();
		} catch (final Throwable ignored) {
		}
		return false;
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

		if (alive && current_thread != null) {
			try {
				if (!current_thread.isInterrupted()) {
					current_thread.interrupt();
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

		try {
			Thread.sleep(time);
		} catch (final InterruptedException ignored) {
			throw new ThreadDeath();
		}
	}
}
