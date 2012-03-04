package org.powerbot.concurrent.action;

import org.powerbot.concurrent.ContainedTask;
import org.powerbot.concurrent.RunnableTask;
import org.powerbot.concurrent.TaskContainer;
import org.powerbot.lang.Activator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

/**
 * An action manager capable of dispatching actions when activated within the a concurrent environment.
 *
 * @author Timer
 */
public class ActionExecutor extends RunnableTask implements ActionContainer {
	private TaskContainer processor;
	private List<Action> actions;
	private State state;

	/**
	 * Initializes this action manager with appropriate objects.
	 *
	 * @param processor The <code>TaskProcessor</code> to use as a medium for processing.
	 */
	public ActionExecutor(TaskContainer processor) {
		this.processor = processor;
		this.actions = new ArrayList<Action>();
		state = State.DESTROYED;
	}

	/**
	 * {@inheritDoc}
	 */
	public void listen() {
		if (state != State.LISTENING) {
			State previous = state;
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
	}

	/**
	 * {@inheritDoc}
	 */
	public void append(Action action) {
		if (!this.actions.contains(action)) {
			this.actions.add(action);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void omit(Action action) {
		this.actions.remove(action);
	}

	/**
	 * Handles the dispatching of actions within the given container.
	 */
	public void run() {
		while (state != State.DESTROYED) {
			if (state == State.LOCKED) {
				synchronized (this) {
					try {
						wait();
					} catch (InterruptedException ignored) {
					}
				}
			} else if (state == State.LISTENING) {
				for (Action action : actions) {
					Activator activator = action.activator;
					if (activator != null && activator.dispatch()) {
						final List<Future<?>> futures = Collections.synchronizedList(new ArrayList<Future<?>>());
						if (action.actionComposite != null) {
							if (action.actionComposite.tasks != null) {
								for (ContainedTask task : action.actionComposite.tasks) {
									if (task != null) {
										processor.submit(task);
										if (task.future != null) {
											futures.add(task.future);
										}
									}
								}
							}
						}
						State previous = state;
						if (action.requireLock) {
							synchronized (this) {
								processor.submit(createWait(futures, this));
								state = State.LOCKED;
								if (futures.size() > 0) {
									try {
										wait();
									} catch (InterruptedException ignored) {
									}
								}
							}
							if (state == State.LOCKED) {
								state = previous;
							}
						}
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

	private RunnableTask createWait(final List<Future<?>> lockingFutures, final Object threadObject) {
		return new RunnableTask() {
			public void run() {
				while (lockingFutures.size() > 0) {
					Future<?> future = lockingFutures.get(0);
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
	private enum State {
		LISTENING, LOCKED, DESTROYED
	}
}
