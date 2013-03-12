package org.powerbot.script;

import java.util.Collection;
import java.util.EventListener;
import java.util.concurrent.FutureTask;

import org.powerbot.script.util.ScriptController;

/**
 * A stateful task based action driver.
 *
 * @author Paris
 */
public interface Script extends Runnable, EventListener {

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

	/**
	 * Retrieves the {@code ScriptController}.
	 * @return the attached {@code ScriptController}
	 */
	public ScriptController getScriptController();

	/**
	 * Sets the {@code ScriptController}.
	 * @param controller the {@code ScriptController} to attach
	 */
	public void setScriptController(ScriptController controller);
}
