package org.powerbot.script;

import java.util.EventListener;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

import org.powerbot.script.lang.Stoppable;
import org.powerbot.script.lang.Suspendable;
import org.powerbot.script.rs3.tools.MethodContext;

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
	 * A reference to {@link Controller} by proxy.
	 *
	 * @see {@link org.powerbot.script.Script.Controller#getContext()}
	 */
	static final BlockingQueue<Controller> controllerProxy = new SynchronousQueue<Controller>();

	/**
	 * Returns the execution queue.
	 *
	 * @param state the {@link State} to query
	 * @return a {@link Queue} of {@link Runnable}s in this {@link Script}s execution queue
	 */
	public Queue<Runnable> getExecQueue(State state);

	/**
	 * Returns the {@link Controller} associated with this {@link Script}
	 *
	 * @return the {@link Controller}
	 */
	public Controller getController();

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

			/**
			 * Creates a new child thread.
			 *
			 * @param e a runnable to be executed by new thread instance
			 * @return constructed thread
			 */
			public Thread newThread(E e);
		}

		/**
		 * Returns the linked {@link MethodContext}.
		 *
		 * @return the {@link MethodContext}
		 */
		public MethodContext getContext();
	}
}
