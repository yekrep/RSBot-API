package org.powerbot.game.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.powerbot.concurrent.LoopTask;
import org.powerbot.concurrent.strategy.Strategy;
import org.powerbot.concurrent.strategy.StrategyGroup;
import org.powerbot.core.script.job.Job;
import org.powerbot.core.script.job.Task;
import org.powerbot.util.Configuration;

/**
 * @author Timer
 */
@Deprecated
public abstract class ActiveScript extends org.powerbot.core.script.ActiveScript {
	public final long started;
	private final List<LoopTask> loopTasks;
	private final List<Strategy> strategies;
	private int iterationSleep = 200;

	public ActiveScript() {
		started = System.currentTimeMillis();
		loopTasks = Collections.synchronizedList(new ArrayList<LoopTask>());
		strategies = Collections.synchronizedList(new ArrayList<Strategy>());

		getStartupJobs().add(new Task() {
			@Override
			public void execute() {
				setup();
			}
		});
	}

	public final void provide(final Strategy strategy) {
		if (!strategies.contains(strategy)) {
			strategies.add(strategy);
		}
	}

	public final void provide(final StrategyGroup group) {
		for (final Strategy strategy : group) {
			provide(strategy);
		}
	}

	public final void revoke(final Strategy strategy) {
		strategies.remove(strategy);
	}

	public final void revoke(final StrategyGroup group) {
		for (final Strategy strategy : group) {
			revoke(strategy);
		}
	}

	public final Job submit(final Runnable task) {
		final Task job = new Task() {
			@Override
			public void execute() {
				task.run();
			}
		};
		getContainer().submit(job);
		return job;
	}

	public final org.powerbot.core.script.job.LoopTask submit(final LoopTask loopTask) {
		if (loopTasks.contains(loopTask)) {
			return null;
		}

		final org.powerbot.core.script.job.LoopTask task = new org.powerbot.core.script.job.LoopTask() {
			@Override
			public int loop() {
				return loopTask.loop();//TODO remove from loopTasks when completed
			}
		};
		getContainer().submit(task);
		return task;
	}

	public final void setIterationDelay(final int milliseconds) {
		iterationSleep = milliseconds;
	}

	protected abstract void setup();

	@Override
	public int loop() {
		try {
			final List<Job> jobs = new LinkedList<>();

			final List<Strategy> strategies_clone = new ArrayList<Strategy>();
			strategies_clone.addAll(strategies);
			for (final Strategy strategy : strategies_clone) {
				if (getContainer().isPaused()) {
					break;
				}
				if (strategy.tasks == null || !strategy.validate() ||
						(strategy.sync && !strategy.isIdle())) {
					continue;
				}
				for (final Runnable task : strategy.tasks) {
					final Job job = new Task() {
						@Override
						public void execute() {
							task.run();
						}
					};
					getContainer().submit(job);
					jobs.add(job);
				}
				strategy.executingJobs = jobs.toArray(new Job[jobs.size()]);
				if (strategy.lock) {
					getContainer().submit(createFutureDisposer(jobs, this));
					awaitNotify(jobs, this);
				}
				if (strategy.reset) {
					break;
				}
			}
			Task.sleep(iterationSleep);
		} catch (final ThreadDeath death) {
			return -1;
		} catch (final Exception t) {
			if (Configuration.DEVMODE) {
				t.printStackTrace();
			}
		}
		return 0;
	}

	private Job createFutureDisposer(final List<Job> jobs, final Object threadObject) {
		return new Task() {
			public void execute() {
				while (jobs.size() > 0) {
					final Job job = jobs.get(0);
					job.join();

					if (!job.isAlive()) {
						jobs.remove(0);
					}
					Task.sleep(0);
				}
				synchronized (threadObject) {
					threadObject.notify();
				}
			}
		};
	}

	private void awaitNotify(final List<Job> jobs, final Object threadObject) {
		synchronized (threadObject) {
			if (jobs.size() > 0) {
				try {
					wait();
				} catch (final InterruptedException ignored) {
				}
			}
		}
	}
}
