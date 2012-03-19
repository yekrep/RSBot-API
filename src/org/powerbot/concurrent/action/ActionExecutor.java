package org.powerbot.concurrent.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

import org.powerbot.concurrent.Task;
import org.powerbot.concurrent.TaskContainer;
import org.powerbot.lang.Activator;

/**
 * An action manager capable of dispatching actions when activated within the a concurrent environment.
 *
 * @author Timer
 */
public class ActionExecutor extends Task implements ActionContainer {
	private final TaskContainer processor;
	private final List<Action> actions;
	public State state;

	/**
	 * Initializes this action manager with appropriate objects.
	 *
	 * @param processor The <code>TaskProcessor</code> to use as a medium for processing.
	 */
	public ActionExecutor(final TaskContainer processor) {
		this.processor = processor;
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
			processor.submit(this);
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
		State heldState;
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
					final Activator activator = action.activator;
					if (activator == null || !activator.applicable()) {
						continue;
					}
					if (action.taskHolder == null || action.taskHolder.tasks == null) {
						continue;
					}
					for (final Task task : action.taskHolder.tasks) {
						processor.submit(task);
						if (action.requireLock && task.future != null) {
							futures.add(task.future);
						}
					}
					heldState = state;
					if (action.requireLock) {
						synchronized (this) {
							processor.submit(createWait(futures, this));
							state = State.LOCKED;
							if (futures.size() > 0) {
								try {
									wait();
								} catch (final InterruptedException ignored) {
								}
							}
						}
						if (state == State.LOCKED) {
							state = heldState;
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

	/**
	 * Creates a <code>RunnableTask</code> that notifies a thread to awaken when all futures are completed.
	 *
	 * @param lockingFutures The <code>List</code> of Futures to wait for.
	 * @param threadObject   The locking object to notify.
	 * @return The <code>RunnableTask</code> to be submitted.
	 */

	private Task createWait(final List<Future<?>> lockingFutures, final Object threadObject) {
		return new Task() {
			public void run() {
				while (lockingFutures.size() > 0) {
					final Future<?> future = lockingFutures.get(0);
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
		LISTENING, LOCKED, DESTROYED
	}
}
