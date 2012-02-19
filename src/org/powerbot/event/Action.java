package org.powerbot.event;

import org.powerbot.lang.Activator;

/**
 * An action that is performed when it can be activated.
 *
 * @author Timer
 */
public abstract class Action {
	protected boolean requireLock;

	/**
	 * Initializes an action's properties.
	 */
	public Action() {
		requireLock = true;
	}

	/**
	 * Returns the <code>Activator</code> associated with this initialized <code>Action</code>.
	 *
	 * @return The <code>Activator</code> associated with this <code>Action</code>.
	 */
	public abstract Activator getActivator();

	/**
	 * Returns the array of action composites initialized with this <code>Action</code>.
	 *
	 * @return The <code>ActionComposite</code> array associated with this <code>Action</code>.
	 */
	public abstract ActionComposite[] constructComposites();
}
