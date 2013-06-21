package org.powerbot.script.lang;

/**
 * An atomic iteration handler.
 *
 * @param <T> the type being enumerated
 *
 * @author Paris
 */
public interface ChainingIterator<T> {

	/**
	 * A consumer of an iteration.
	 *
	 * @param index the index of the item in its collection
	 * @param item the item
	 * @return {@code true} to continue iterating, otherwise {@code false}
	 */
	public boolean next(int index, T item);
}
