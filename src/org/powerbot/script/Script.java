package org.powerbot.script;

import java.util.EventListener;
import java.util.Queue;

import org.powerbot.script.lang.Stoppable;
import org.powerbot.script.lang.Suspendable;

/**
 * The base interface of a script.
 */
public interface Script extends Runnable, EventListener {
	/**
	 * The representative states of a {@link Script}
	 */
	public enum State {
		START, SUSPEND, RESUME, STOP
	}

	/**
	 * Returns the execution queue.
	 *
	 * @param state the {@link State} to query
	 * @return a {@link Queue} of {@link Runnable}s in this {@link Script}s execution queue
	 */
	public Queue<Runnable> getExecQueue(State state);

	/**
	 * A controller for a {@link Script} which invokes and determines state changes.
	 */
	public interface Controller extends Suspendable, Stoppable {
		/**
		 * Returns the executor.
		 *
		 * @return the executor
		 */
		public Executor<Runnable> getExecutor();

		/**
		 * An execution manager for a {@link org.powerbot.script.Script.Controller}
		 *
		 * @param <E> a {@link java.lang.Runnable}
		 */
		public interface Executor<E extends Runnable> {
			/**
			 * Adds a runnable to the executor.
			 *
			 * @param e a runnable to be executed
			 * @return {@code true} if the runnable was added, otherwise {@code} false
			 */
			public boolean offer(E e);
		}
	}
}
