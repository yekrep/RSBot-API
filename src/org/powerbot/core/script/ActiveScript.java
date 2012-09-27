package org.powerbot.core.script;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.powerbot.core.script.job.Container;
import org.powerbot.core.script.job.Job;
import org.powerbot.core.script.job.JobListener;
import org.powerbot.core.script.job.LoopTask;
import org.powerbot.core.script.job.Task;
import org.powerbot.core.script.job.TaskContainer;

/**
 * @author Timer
 */
public abstract class ActiveScript extends LoopTask implements Script {
	public final Logger log = Logger.getLogger(getClass().getName());

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
	@Override
	public final List<Job> getStartupJobs() {
		return startup_jobs;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void start() {
		if (container.isShutdown()) {
			return;
		}

		final List<Job> startup_jobs = getStartupJobs();
		if (!startup_jobs.contains(this)) {
			startup_jobs.add(new Task() {
				@Override
				public void execute() {
					onStart();
				}
			});
			startup_jobs.add(this);
		}
		container.addListener(stop_listener);

		for (final Job job : Collections.unmodifiableList(startup_jobs)) {
			container.submit(job);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isActive() {
		return !container.isTerminated();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setPaused(final boolean paused) {
		container.setPaused(paused);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isPaused() {
		return container.isPaused();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void shutdown() {
		if (!isShutdown()) {
			container.submit(new Task() {
				@Override
				public void execute() {
					onStop();
				}
			});
			container.shutdown();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isShutdown() {
		return container.isShutdown();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void stop() {
		container.interrupt();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Container getContainer() {
		return container;
	}

	public void onStart() {
	}

	public void onStop() {
	}
}
