package org.powerbot.script;

/**
 * Viewable
 * An object which can be rendered in the viewport.
 */
public interface Viewable {
	/**
	 * Returns {@code true} if the object is currently rendered in the viewport.
	 *
	 * @return {@code true} if the object is currently rendered in the viewport, otherwise {@code false}.
	 */
	boolean inViewport();

	/**
	 * Query
	 *
	 * @param <T>
	 */
	interface Query<T> {
		T viewable();
	}
}
