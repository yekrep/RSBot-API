package org.powerbot.script;

import java.util.concurrent.ExecutorService;

/**
 * A {@code Script} controller.
 *
 * @author Paris
 */
public interface ScriptController extends Stoppable, Suspendable {

	/**
	 * Retrieves the attached {@code ExecutorService}.
	 *
	 * @return an {@code ExecutorService}
	 */
	public ExecutorService getExecutorService();
}
