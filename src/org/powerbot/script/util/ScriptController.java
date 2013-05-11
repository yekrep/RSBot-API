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
	 * If the queue is not empty only {@link org.powerbot.script.Script}s in the queue will be executed.
	 * In effect, all other {@link org.powerbot.script.Script}s will be paused.
	 *
	 * @return a queue
	 */
	public Queue<Script> getLockQueue();
}
