package org.powerbot.script.util;

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
}
