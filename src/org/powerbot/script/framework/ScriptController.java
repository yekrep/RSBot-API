package org.powerbot.script.framework;

import java.util.Queue;

import org.powerbot.script.Script;

/**
 * A {@code Script} controller.
 *
 * @author Paris
 */
public interface ScriptController extends Stoppable, Suspendable {

	/**
	 * Retrieves the attached {@code ExecutorDispatch}.
	 *
	 * @return an {@code ExecutorDispatch}
	 */
	public ExecutorDispatch<Boolean> getExecutorService();

	/**
	 * Retrieves a locking queue.
	 * <p/>
	 * If the queue is not empty only {@link org.powerbot.script.Script}s in the queue will be executed.
	 * In effect, all other {@link org.powerbot.script.Script}s will be paused.
	 *
	 * @return a queue
	 */
	public Queue<Script> getLockQueue();

	/**
	 * Gets all the scripts.
	 *
	 * @return the queue of scripts
	 */
	Iterable<ScriptDefinition> getScripts();
}
