package org.powerbot.script.lang;

/**
 * A chaining iterator consumer class.
 *
 * @param <T> the enumerated type
 *
 * @author Paris
 */
public interface ChainingIterator<T> {

	/**
	 * An atomic consumer of an iterator.
	 *
	 * @param index the index of this item in its parent collection
	 * @param item the item
	 * @return {@code true} to continue iterating, otherwise {@code false}
	 */
	public boolean next(int index, T item);
}
