package org.powerbot.script.util;

import org.powerbot.script.Script;

import java.util.Queue;

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
	 *
	 * @return a queue
	 */
	public Queue<Script> getLockQueue();
}
