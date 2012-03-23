package org.powerbot.lang;

/**
 * An interface that represents something that can be activated when conditions are met.
 *
 * @author Timer
 */
public interface Activatable {
	/**
	 * Determines whether or not to act upon the desired object after the activation interface is invoked.
	 *
	 * @return <tt>true</tt> if dispatching should proceed; otherwise <tt>false</tt>, causing denial of object processing.
	 */
	public boolean applicable();
}
