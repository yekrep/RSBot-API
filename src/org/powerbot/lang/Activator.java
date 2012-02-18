package org.powerbot.lang;

/**
 * An interface that represents something that can be activated when conditions are met.
 *
 * @author Timer
 */
public interface Activator {
	/**
	 * Determines whether or not to dispatch the desired object after the activation interface is invoked.
	 *
	 * @return <tt>true</tt> if dispatching should proceed; otherwise <tt>false</tt>, causing denial of object processing.
	 */
	public boolean dispatch();
}
