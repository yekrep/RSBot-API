package org.powerbot.script;

/**
 * Describes an object which can be rendered in the viewport.
 */
public interface Viewport {
	/**
	 * Returns {@code true} if the object is currently rendered in the viewport.
	 *
	 * @return {@code true} if the object is currently rendered in the viewport, otherwise {@code false}.
	 */
	public boolean inViewport();

	public interface Query<T> {
		public T viewable();
	}
}
