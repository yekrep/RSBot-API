package org.powerbot.event;

import org.powerbot.lang.Activator;

/**
 * An action that is performed when it can be activated.
 *
 * @author Timer
 */
public class Action {
	public boolean requireLock;
	public Activator activator;
	public ActionComposite[] actionComposites;

	/**
	 * Initializes this <code>Action</code> with appropriate information required for processing.
	 *
	 * @param activator        The <code>Activator</code> associated with this <code>Action</code>.
	 * @param actionComposites The <code>ActionComposite</code> array associated with this <code>Action</code>.
	 */
	public Action(Activator activator, ActionComposite[] actionComposites) {
		this.activator = activator;
		this.actionComposites = actionComposites;
		this.requireLock = true;
	}

	/**
	 * Initializes this <code>Action</code> with appropriate information required for processing.
	 *
	 * @param activator        The <code>Activator</code> associated with this <code>Action</code>.
	 * @param actionComposites The <code>ActionComposite</code> array associated with this <code>Action</code>.
	 * @param requireLock      <tt>true</tt> to require the ActionManager to lock while processing this action; otherwise <tt>false</tt>.
	 */
	public Action(Activator activator, ActionComposite[] actionComposites, boolean requireLock) {
		this.activator = activator;
		this.actionComposites = actionComposites;
		this.requireLock = requireLock;
	}
}
