package org.powerbot.script.methods;

import org.powerbot.script.wrappers.Displayable;

public interface Resizable {
	/**
	 * Determines if the target is collapsed.
	 *
	 * @return <tt>true</tt> if the target is collapsed; otherwise <tt>false</tt>
	 */
	public boolean isCollapsed();

	/**
	 * Scrolls the object into view.
	 *
	 * @param component the {@link Displayable} object to scroll into view
	 * @return <tt>true</tt> if the object is visible; otherwise <tt>false</tt>
	 */
	public boolean scroll(Displayable component);
}
