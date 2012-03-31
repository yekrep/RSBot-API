package org.powerbot.concurrent.strategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.powerbot.concurrent.Task;
import org.powerbot.concurrent.TaskContainer;
import org.powerbot.game.api.util.Time;

/**
 * An action manager capable of dispatching strategies when activated within the a concurrent environment.
 *
 * @author Timer
 */
public class StrategyDaemon implements StrategyContainer, Task {
	private final TaskContainer container;
	private final TaskContainer owner;
	private final List<Strategy> strategies;
	public State state;
	private int iterationSleep = 200;

	/**
	 * Initializes this action manager with appropriate objects.
	 *
	 * @param container The <code>TaskContainer</code> to use as a medium for processing.
	 * @param owner     The <code>TaskContainer</code> that owns this executor.
	 */
	public StrategyDaemon(final TaskContainer container, final TaskContainer owner) {
		this.container = container;
		this.owner = owner;
		strategies = new ArrayList<Strategy>();
		state = State.DESTROYED;
	}

	/**
	 * {@inheritDoc}
	 */
	public void listen() {
		if (state != State.LISTENING) {
			final State previous = state;
			state = State.LISTENING;
			if (previous == State.LOCKED) {
				synchronized (this) {
					notify();
				}
				return;
			}
			owner.submit(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void lock() {
		state = State.LOCKED;
	}

	/**
	 * {@inheritDoc}
	 */
	public void destroy() {
		state = State.DESTROYED;
		synchronized (this) {
			notify();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void append(final Strategy action) {
		if (!strategies.contains(action)) {
			strategies.add(action);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void omit(final Strategy action) {
		strategies.remove(action);
	}

	/**
	 * Handles the dispatching of strategies within the given container.
	 */
	public void run() {
		final List<Future<?>> futures = Collections.synchronizedList(new ArrayList<Future<?>>());
		State cached_state;
		while (state != State.DESTROYED) {
			if (state == State.LOCKED) {
				synchronized (this) {
					try {
						wait();
					} catch (final InterruptedException ignored) {
					}
				}
			} else if (state == State.LISTENING) {
				for (final Strategy strategy : strategies) {
					if (state != State.LISTENING) {
						break;
					}
					if (strategy.tasks == null || !strategy.validate() ||
							(strategy.sync && !strategy.isIdle())) {
						continue;
					}
					for (final Task task : strategy.tasks) {
						final Future<?> future = container.submit(task);
						if (future != null) {
							futures.add(future);
						}
					}
					cached_state = state;
					strategy.executingFutures = futures.toArray(new Future<?>[futures.size()]);
					if (strategy.lock) {
						container.submit(createFutureDisposer(futures, this));
						awaitNotify(futures);
						if (state == State.PROCESSING) {
							state = cached_state;
						}
					}
					futures.clear();
					if (strategy.reset) {
						break;
					}
				}
				Time.sleep(iterationSleep);
			} else {
				throw new RuntimeException("bad action-dispatch state");
			}
		}
	}

	public void setIterationSleep(final int milliseconds) {
		iterationSleep = milliseconds;
	}

	private void awaitNotify(final List<Future<?>> futures) {
		synchronized (this) {
			state = State.PROCESSING;

			if (futures.size() > 0) {
				try {
					wait();
				} catch (final InterruptedException ignored) {
				}
			}
		}
	}

	/**
	 * Creates a <code>RunnableTask</code> that notifies a thread to awaken when all futures are completed.
	 *
	 * @param lockingFutures The <code>List</code> of Futures to wait for.
	 * @param threadObject   The locking object to notify.
	 * @return The <code>RunnableTask</code> to be submitted.
	 */

	private Task createFutureDisposer(final List<Future<?>> lockingFutures, final Object threadObject) {
		return new Task() {
			public void run() {
				while (lockingFutures.size() > 0) {
					final Future<?> future = lockingFutures.get(0);
					try {
						future.get();
					} catch (final InterruptedException ignored) {
					} catch (final ExecutionException ignored) {
					}

					if (future.isDone()) {
						lockingFutures.remove(0);
					}
				}
				synchronized (threadObject) {
					threadObject.notify();
				}
			}
		};
	}

	/**
	 * An enumeration of different states this <code>ActionDispatcher</code> can be in.
	 *
	 * @author Timer
	 */
	public enum State {
		LISTENING, LOCKED, DESTROYED, PROCESSING
	}
}
