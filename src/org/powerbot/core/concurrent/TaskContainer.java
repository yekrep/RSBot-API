package org.powerbot.core.concurrent;

import java.util.LinkedList;
import java.util.List;

/**
 * A fully featured {@link Container} designed to manipulate {@link Job}s.
 *
 * @author Timer
 */
public class TaskContainer implements Container {
	private final List<JobListener> listeners;
	private final Object listeners_lock;

	private final List<Job> jobs;
	private final Object job_lock;

	private final List<Container> children;
	private final Object children_lock;

	private final ThreadGroup group;
	private volatile boolean paused, shutdown, interrupted;

	private final TaskContainer parent_container;

	public TaskContainer() {
		this(Thread.currentThread().getThreadGroup(), null);
	}

	private TaskContainer(final ThreadGroup parent, final TaskContainer parent_container) {
		listeners = new LinkedList<>();
		listeners_lock = new Object();

		jobs = new LinkedList<>();
		job_lock = new Object();

		children = new LinkedList<>();
		children_lock = new Object();

		group = new ThreadGroup(parent, getClass().getName() + "/" + hashCode());
		paused = false;
		shutdown = false;
		interrupted = false;

		this.parent_container = parent_container;
	}

	/**
	 * {@inheritDoc}
	 */
	public final void submit(final Job job) {
		if (shutdown) {
			throw new RejectedJobException(job.getClass().getName() + "/" + job.hashCode() + " rejected from " + getClass().getName() + "/" + hashCode() + " because the container is shutdown");
		}

		job.setContainer(this);
		new Thread(
				group,
				createWorker(job),
				group.getName() + "@" + job.getClass().getName() + "/" + job.hashCode()
		).start();
	}

	/**
	 * {@inheritDoc}
	 */
	public void setPaused(final boolean paused) {
		if (this.paused != paused) {
			this.paused = paused;
		}

		for (final Container container : getChildren()) {
			container.setPaused(paused);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isPaused() {
		return paused;
	}

	/**
	 * {@inheritDoc}
	 */
	public final Job[] enumerate() {
		synchronized (job_lock) {
			final int size;
			if ((size = jobs.size()) > 0) {
				final Job[] enumerated_jobs = new Job[size];
				return jobs.toArray(enumerated_jobs);
			}
		}
		return new Job[0];
	}

	/**
	 * {@inheritDoc}
	 */
	public final int getActiveCount() {
		synchronized (job_lock) {
			return jobs.size();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public final Container branch() {
		final Container container = new TaskContainer(group, this);
		synchronized (children_lock) {
			children.add(container);
		}
		return container;
	}

	/**
	 * {@inheritDoc}
	 */
	public final Container[] getChildren() {
		synchronized (children_lock) {
			final int size;
			if ((size = children.size()) > 0) {
				final Container[] children = new Container[size];
				return this.children.toArray(children);
			}
		}
		return new Container[0];
	}

	/**
	 * {@inheritDoc}
	 */
	public final void shutdown() {
		if (!isShutdown()) {
			shutdown = true;
		}

		for (final Container container : getChildren()) {
			container.shutdown();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public final boolean isShutdown() {
		return shutdown;
	}

	/**
	 * {@inheritDoc}
	 */
	public final void interrupt() {
		shutdown();

		interrupted = true;
		for (final Container container : getChildren()) {
			container.interrupt();
		}

		final Job[] jobs = enumerate();
		for (final Job job : jobs) {
			job.interrupt();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public final boolean isTerminated() {
		return (shutdown || interrupted) && getActiveCount() == 0;
	}

	/**
	 * {@inheritDoc}
	 */
	public final void addListener(final JobListener listener) {
		synchronized (listeners_lock) {
			if (!listeners.contains(listener)) {
				listeners.add(listener);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public final void removeListener(final JobListener listener) {
		synchronized (listeners_lock) {
			listeners.remove(listener);
		}
	}

	private Runnable createWorker(final Job job) {
		return new Runnable() {
			public void run() {
				synchronized (job_lock) {
					jobs.add(job);
				}
				notifyListeners(job, true);
				try {
					job.work();
				} catch (final Throwable ignored) {
					//TODO uncaught exception
				}
				synchronized (job_lock) {
					jobs.remove(job);
				}
				notifyListeners(job, false);
				job.setContainer(null);
			}
		};
	}

	private void notifyListeners(final Job job, final boolean started) {
		synchronized (listeners_lock) {
			for (final JobListener listener : listeners) {
				try {
					if (started) {
						listener.jobStarted(job);
					} else {
						listener.jobStopped(job);
					}
				} catch (final Throwable ignored) {
				}
			}
		}

		if (parent_container != null) {
			parent_container.notifyListeners(job, started);
		}
	}
}
