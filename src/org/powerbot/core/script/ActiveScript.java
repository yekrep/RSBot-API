package org.powerbot.core.script;

import java.util.LinkedList;
import java.util.List;

import org.powerbot.core.concurrent.Container;
import org.powerbot.core.concurrent.DuplicateJobException;
import org.powerbot.core.concurrent.Job;
import org.powerbot.core.concurrent.JobListener;
import org.powerbot.core.concurrent.Task;
import org.powerbot.core.concurrent.TaskContainer;
import org.powerbot.core.script.job.LoopTask;

/**
 * @author Timer
 */
public abstract class ActiveScript extends LoopTask implements Script {
	private final Container container;
	private final List<Job> startup_jobs;

	private final JobListener stop_listener;

	public ActiveScript() {
		container = new TaskContainer();
		startup_jobs = new LinkedList<>();

		stop_listener = new JobListener() {
			@Override
			public void jobStarted(final Job job) {
			}

			@Override
			public void jobStopped(final Job job) {
				if (job.equals(ActiveScript.this)) {
					shutdown();
				}
			}
		};
	}

	/**
	 * {@inheritDoc}
	 */
	public final List<Job> getStartupJobs() {
		return startup_jobs;
	}

	/**
	 * {@inheritDoc}
	 */
	public final void start() {
		if (container.isShutdown()) {
			return;
		}

		final List<Job> startup_jobs = getStartupJobs();
		if (!startup_jobs.contains(this)) {
			startup_jobs.add(this);
			startup_jobs.add(new Task() {
				@Override
				public void execute() {
					onStart();
				}
			});
		}
		container.addListener(stop_listener);

		for (final Job job : startup_jobs) {
			try {
				container.submit(job);
			} catch (final DuplicateJobException ignored) {
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public final boolean isActive() {
		return !container.isTerminated();
	}

	/**
	 * {@inheritDoc}
	 */
	public final void setPaused(final boolean paused) {
		container.setPaused(paused);
	}

	/**
	 * {@inheritDoc}
	 */
	public final boolean isPaused() {
		return container.isPaused();
	}

	/**
	 * {@inheritDoc}
	 */
	public final void shutdown() {
		container.shutdown();
	}

	/**
	 * {@inheritDoc}
	 */
	public final boolean isShutdown() {
		return container.isShutdown();
	}

	/**
	 * {@inheritDoc}
	 */
	public final void stop() {
		container.interrupt();
	}

	/**
	 * {@inheritDoc}
	 */
	public final Container getContainer() {
		return container;
	}

	public void onStart() {
	}
}
