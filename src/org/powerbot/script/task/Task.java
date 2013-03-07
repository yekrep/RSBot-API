package org.powerbot.script.task;

import java.util.EventListener;
import java.util.concurrent.Future;

import org.powerbot.game.api.util.Random;

public abstract class Task implements Runnable, EventListener {
	private TaskContainer container;
	private Thread thread;
	private Future<?> future;

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

	public abstract void execute();

	@Override
	public final void run() {
		if (this.thread != null) throw new IllegalStateException(getClass().getName() + " is already running");

		this.thread = Thread.currentThread();
		final TaskListener listener = container instanceof TaskListener ? (TaskListener) container : null;
		if (listener != null) listener.taskStarted(this);
		try {
			execute();
		} catch (final ThreadDeath ignored) {
		} catch (final Throwable e) {
			e.printStackTrace();
		}
		if (listener != null) listener.taskStopped(this);
		this.thread = null;
		setContainer(null);
	}

	public final void join() {
		if (future != null) try {
			future.get();
		} catch (final ThreadDeath death) {
			throw death;
		} catch (final Throwable e) {
		}
	}

	public final boolean isActive() {
		return future != null && !future.isDone();
	}

	public final void interrupt() {
		if (future != null) future.cancel(true);
	}

	public final TaskContainer getContainer() {
		return this.container;
	}

	public final void setContainer(final TaskContainer container) {
		this.container = container;
	}

	public final Future<?> getFuture() {
		return this.future;
	}

	public final void setFuture(final Future<?> future) {
		this.future = future;
	}

	public final Thread getThread() {
		return thread;
	}
}
