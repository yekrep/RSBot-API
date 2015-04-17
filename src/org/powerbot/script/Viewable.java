package org.powerbot.script;

/**
 * Describes an object which can be rendered in the viewport.
 */
public interface Viewable {
	/**
	 * Returns {@code true} if the object is currently rendered in the viewport.
	 *
	 * @return {@code true} if the object is currently rendered in the viewport, otherwise {@code false}.
	 */
	boolean inViewport();

	interface Query<T> {
		T viewable();
	}
}
