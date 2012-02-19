package org.powerbot.event;

/**
 * A action manager that is able to perform basic operations to a thread dispatching actions.
 *
 * @author Timer
 */
public interface ActionManager {
	/**
	 * Begins listening to the actions associated with this <code>ActionManager</code>.
	 */
	public void listen();

	/**
	 * Locks this manager from processing and dispatching of actions.
	 */
	public void lock();

	/**
	 * Destroys this <code>ActionManager</code> and cleans up.
	 */
	public void destroy();

	/**
	 * Begins listening on an action for appropriate dispatching.
	 *
	 * @param action The <code>Action</code> to handle.
	 */
	public void handle(Action action);

	/**
	 * Terminates listening and dispatch of the specified <code>Action</code>.
	 *
	 * @param action The <code>Action</code> to lose handle of.
	 */
	public void lose(Action action);
}
