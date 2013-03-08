package org.powerbot.script;

import java.util.Collection;
import java.util.concurrent.FutureTask;

/**
 * A stateful task based action driver.
 *
 * @author Paris
 */
public interface Script extends Runnable {

	public enum State { START, STOP, SUSPEND, RESUME };

	/**
	 * Retrieves a list of tasks for the specified state.
	 * @param state the query state
	 * @return the set of tasks for the requested {@code state}
	 */
	public Collection<FutureTask<Boolean>> getTasks(State state);

	/**
	 * Determines the overall order of priority of this script.
	 * @return the absolute priority on the integer scale
	 */
	public int getPriority();
}
