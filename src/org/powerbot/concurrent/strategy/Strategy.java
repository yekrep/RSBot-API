package org.powerbot.concurrent.strategy;

import java.util.EventListener;
import java.util.concurrent.Future;

/**
 * A strategy that is performed when its policy allows so.
 *
 * @author Timer
 */
@Deprecated
public class Strategy implements Condition, EventListener {
	boolean lock;
	boolean reset;
	boolean sync;

	Runnable[] tasks;
	Future<?>[] executingFutures;

	private Condition policy;

	public Strategy() {
		this((Runnable) null);
		if (this instanceof Runnable) {
			setTask((Runnable) this);
		}
	}

	/**
	 * Initializes this <code>Strategy</code> with appropriate information required for processing.
	 *
	 * @param task The task associated with this <code>Strategy</code>.
	 */
	public Strategy(final Runnable task) {
		this(null, new Runnable[]{task});
	}

	/**
	 * Initializes this <code>Strategy</code> with appropriate information required for processing.
	 *
	 * @param tasks The tasks associated with this <code>Strategy</code>.
	 */
	public Strategy(final Runnable[] tasks) {
		this(null, tasks);
	}

	/**
	 * Initializes this <code>Strategy</code> with appropriate information required for processing.
	 *
	 * @param policy The policy associated with this <code>Strategy</code>.
	 * @param task   The task associated with this <code>Strategy</code>.
	 */
	public Strategy(final Condition policy, final Runnable task) {
		this(policy, new Runnable[]{task});
	}

	/**
	 * Initializes this <code>Strategy</code> with appropriate information required for processing.
	 *
	 * @param policy The policy associated with this <code>Strategy</code>.
	 * @param tasks  The tasks associated with this <code>Strategy</code>.
	 */
	public Strategy(final Condition policy, final Runnable[] tasks) {
		this.policy = policy;
		this.tasks = tasks;

		lock = true;
		reset = false;
		sync = true;
		executingFutures = null;
	}

	public boolean validate() {
		if (policy != null) {
			return policy.validate();
		}
		throw new RuntimeException("unable to validate this strategy (missing policy)");
	}

	boolean isIdle() {
		if (executingFutures != null) {
			for (final Future<?> future : executingFutures) {
				if (!future.isDone()) {
					return false;
				}
			}
		}
		return true;
	}

	public void setTasks(final Runnable[] tasks) {
		this.tasks = tasks;
	}

	public void setTask(final Runnable task) {
		setTasks(new Runnable[]{task});
	}

	public void setLock(final boolean lock) {
		this.lock = lock;
	}

	public void setReset(final boolean reset) {
		this.reset = reset;
	}

	public void setSync(final boolean sync) {
		this.sync = sync;
	}
}
