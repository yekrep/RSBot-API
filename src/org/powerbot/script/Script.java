package org.powerbot.script;

import java.util.EventListener;
import java.util.Queue;
import java.util.concurrent.BlockingDeque;

import org.powerbot.script.lang.Stoppable;
import org.powerbot.script.lang.Suspendable;
import org.powerbot.script.methods.MethodContext;

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
	 * Sets a new {@link Controller} for this {@link Script}
	 *
	 * @param controller the new {@link Controller}
	 */
	public void setController(Controller controller);

	/**
	 * Returns the {@link Controller} associated with this {@link Script}
	 *
	 * @return the {@link Controller}
	 */
	public Controller getController();

	/**
	 * Sets a new {@link MethodContext} for this {@link Script}
	 *
	 * @param ctx the new {@link MethodContext}
	 */
	public void setContext(MethodContext ctx);

	/**
	 * Returns the {@link MethodContext} associated with this {@link Script}
	 *
	 * @return the {@link MethodContext}
	 */
	public MethodContext getContext();

	/**
	 * A controller for a {@link Script} which invokes and determines state changes.
	 */
	public interface Controller extends Suspendable, Stoppable {
		/**
		 * Returns the executor queue.
		 *
		 * @return the executor queue
		 */
		public BlockingDeque<Runnable> getExecutor();
	}
}
