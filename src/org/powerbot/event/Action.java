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
	public ActionComposite actionComposite;

	/**
	 * Initializes this <code>Action</code> with appropriate information required for processing.
	 *
	 * @param activator       The <code>Activator</code> associated with this <code>Action</code>.
	 * @param actionComposite The <code>ActionComposite</code> associated with this <code>Action</code>.
	 */
	public Action(Activator activator, ActionComposite actionComposite) {
		this.activator = activator;
		this.actionComposite = actionComposite;
		this.requireLock = true;
	}

	/**
	 * Initializes this <code>Action</code> with appropriate information required for processing.
	 *
	 * @param activator       The <code>Activator</code> associated with this <code>Action</code>.
	 * @param actionComposite The <code>ActionComposite</code> array associated with this <code>Action</code>.
	 * @param requireLock     <tt>true</tt> to require the ActionManager to lock while processing this action; otherwise <tt>false</tt>.
	 */
	public Action(Activator activator, ActionComposite actionComposite, boolean requireLock) {
		this.activator = activator;
		this.actionComposite = actionComposite;
		this.requireLock = requireLock;
	}
}
