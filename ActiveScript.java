package org.powerbot.game.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EventListener;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.powerbot.concurrent.LoopTask;
import org.powerbot.concurrent.Processor;
import org.powerbot.concurrent.ThreadPool;
import org.powerbot.concurrent.strategy.DaemonState;
import org.powerbot.concurrent.strategy.Strategy;
import org.powerbot.concurrent.strategy.StrategyDaemon;
import org.powerbot.concurrent.strategy.StrategyGroup;
import org.powerbot.core.script.job.Task;
import org.powerbot.event.EventManager;
import org.powerbot.game.bot.Context;

/**
 * @author Timer
 */
@Deprecated
public abstract class ActiveScript extends org.powerbot.core.script.ActiveScript implements Processor {
	public final long started;

	private EventManager eventManager;
	private ThreadPoolExecutor executor;
	private StrategyDaemon strategyDaemon;
	private final List<LoopTask> loopTasks;
	private final List<EventListener> listeners;

	private Context context;

	public ActiveScript() {
		started = System.currentTimeMillis();
		eventManager = null;
		executor = null;
		strategyDaemon = null;
		loopTasks = Collections.synchronizedList(new ArrayList<LoopTask>());
		listeners = Collections.synchronizedList(new ArrayList<EventListener>());

		getStartupJobs().add(new Task() {
			@Override
			public void execute() {
				setup();
				resume();
				if (context != null) {
					context.ensureAntiRandoms();
				}
			}
		});
	}

	public final void init(final Context context) {
		this.context = context;
		eventManager = context.getEventManager();
		executor = new ThreadPoolExecutor(1, Integer.MAX_VALUE, 60, TimeUnit.HOURS, new SynchronousQueue<Runnable>(), new ThreadPool(context.getThreadGroup()), new ThreadPoolExecutor.CallerRunsPolicy());
		strategyDaemon = new StrategyDaemon(executor, context.getContainer());
	}

	public final void provide(final Strategy strategy) {
		strategyDaemon.append(strategy);

		if (!listeners.contains(strategy)) {
			listeners.add(strategy);
			if (!isLocked()) {
				eventManager.accept(strategy);
			}
		}
	}

	public final void provide(final StrategyGroup group) {
		for (final Strategy strategy : group) {
			provide(strategy);
		}
	}

	public final void revoke(final Strategy strategy) {
		strategyDaemon.omit(strategy);

		listeners.remove(strategy);
		eventManager.remove(strategy);
	}

	public final void revoke(final StrategyGroup group) {
		for (final Strategy strategy : group) {
			revoke(strategy);
		}
	}

	public final Future<?> submit(final Runnable task) {
		return executor.submit(task);
	}

	public final boolean submit(final LoopTask loopTask) {
		if (loopTasks.contains(loopTask)) {
			return false;
		}

		loopTask.init(this);
		loopTask.start();
		loopTasks.add(loopTask);
		listeners.add(loopTask);
		eventManager.accept(loopTask);
		executor.submit(loopTask);
		return true;
	}

	public final void terminated(final Runnable task) {
		if (task instanceof LoopTask) {
			final LoopTask loopTask = (LoopTask) task;
			listeners.remove(loopTask);
			eventManager.remove(loopTask);
		}
	}

	public final void setIterationDelay(final int milliseconds) {
		strategyDaemon.setIterationSleep(milliseconds);
	}

	protected abstract void setup();

	@Override
	public int loop() {
		return 2000;
	}

	public final void resume() {
		eventManager.accept(ActiveScript.this);
		final List<LoopTask> cache_list = new ArrayList<>();
		cache_list.addAll(loopTasks);
		for (final LoopTask task : cache_list) {
			if (task.isKilled()) {
				loopTasks.remove(task);
				continue;
			}

			if (!task.isRunning()) {
				task.start();
				executor.submit(task);
			}
		}
		for (final EventListener eventListener : listeners) {
			eventManager.accept(eventListener);
		}
		strategyDaemon.listen();
	}

	public final void pause() {
		pause(false);
	}

	public final void pause(final boolean removeListener) {
		strategyDaemon.lock();
		for (final LoopTask task : loopTasks) {
			task.stop();
		}
		if (removeListener) {
			eventManager.remove(ActiveScript.this);
			for (final EventListener eventListener : listeners) {
				eventManager.remove(eventListener);
			}
		}
	}

	public final void askStop() {
		if (!executor.isShutdown()) {
			executor.submit(new Runnable() {
				public void run() {
					onStop();
				}
			});
		}
		eventManager.remove(ActiveScript.this);
		for (final LoopTask task : loopTasks) {
			task.stop();
			task.kill();
		}
		loopTasks.clear();
		for (final EventListener eventListener : listeners) {
			eventManager.remove(eventListener);
		}
		listeners.clear();
		strategyDaemon.destroy();
		executor.shutdown();

		final String name = Thread.currentThread().getThreadGroup().getName();
		if (name.startsWith("GameDefinition-") ||
				name.startsWith("ThreadPool-")) {
			context.updateControls();
		}
	}

	public void onStop() {
	}

	public final void kill() {
		executor.shutdownNow();
	}

	public final DaemonState getState() {
		return strategyDaemon.state;
	}

	public final boolean isRunning() {
		return getState() != DaemonState.DESTROYED;
	}

	public final boolean isLocked() {
		return getState() == DaemonState.LOCKED;
	}

	public final ThreadPoolExecutor getStrategyDaemon() {
		return executor;
	}
}
