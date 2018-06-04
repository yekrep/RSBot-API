package org.powerbot.script.rt6;

/**
 * Resizable
 * An object which contains displayable components which can be resized and can scroll components into view.
 */
public interface Resizable {
	/**
	 * Determines if the target is collapsed.
	 *
	 * @return {@code true} if the target is collapsed; otherwise {@code false}
	 */
	boolean collapsed();

	/**
	 * Scrolls the object into view.
	 *
	 * @param component the {@link Displayable} object to scroll into view
	 * @return {@code true} if the object is visible; otherwise {@code false}
	 */
	boolean scroll(Displayable component);
}
