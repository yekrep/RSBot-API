package org.powerbot.script;

import org.powerbot.script.internal.ScriptController;
import org.powerbot.script.methods.MethodContext;

import java.util.EventListener;
import java.util.Queue;
import java.util.concurrent.Callable;

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
	 * Sets a new {@link ScriptController} for this {@link Script}
	 *
	 * @param container the new {@link ScriptController}
	 */
	public void setController(ScriptController container);

	/**
	 * Returns the {@link ScriptController} associated with this {@link Script}
	 *
	 * @return the {@link ScriptController}
	 */
	public ScriptController getController();

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
}
