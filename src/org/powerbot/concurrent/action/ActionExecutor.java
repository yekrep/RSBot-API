package org.powerbot.concurrent.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

import org.powerbot.concurrent.Task;
import org.powerbot.concurrent.TaskContainer;
import org.powerbot.game.api.util.Time;
import org.powerbot.lang.Activatable;

/**
 * An action manager capable of dispatching actions when activated within the a concurrent environment.
 *
 * @author Timer
 */
public class ActionExecutor implements ActionContainer, Task {
	private final TaskContainer container;
	private final TaskContainer owner;
	private final List<Action> actions;
	public State state;

	/**
	 * Initializes this action manager with appropriate objects.
	 *
	 * @param container The <code>TaskContainer</code> to use as a medium for processing.
	 * @param owner     The <code>TaskContainer</code> that owns this executor.
	 */
	public ActionExecutor(final TaskContainer container, final TaskContainer owner) {
		this.container = container;
		this.owner = owner;
		actions = new ArrayList<Action>();
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
	public void append(final Action action) {
		if (!actions.contains(action)) {
			actions.add(action);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void omit(final Action action) {
		actions.remove(action);
	}

	/**
	 * Handles the dispatching of actions within the given container.
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
				for (final Action action : actions) {
					if (state != State.LISTENING) {
						break;
					}
					final Activatable activator = action.activator;
					if (activator == null || action.tasks == null || !activator.applicable()) {
						continue;
					}
					if (action.synchronizeInstances && !action.isIdle()) {
						continue;
					}
					for (final Task task : action.tasks) {
						final Future<?> future = container.submit(task);
						if (future != null) {
							futures.add(future);
						}
					}
					cached_state = state;
					final Task running_action = createFutureDisposer(futures, this);
					action.future = container.submit(running_action);
					if (action.requireLock) {
						awaitNotify(futures);
						if (state == State.PROCESSING) {
							state = cached_state;
						}
					}
					futures.clear();
					if (action.resetExecutionQueue) {
						break;
					}
				}
			} else {
				throw new RuntimeException("bad action-dispatch state");
			}
		}
	}

	public void awaitNotify(final List<Future<?>> futures) {
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
					if (future.isDone()) {
						lockingFutures.remove(0);
					}
					Time.sleep(15);
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
