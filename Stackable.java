package org.powerbot.script;

/**
 * Stackable
 * An item which has a variable quantity.
 */
public interface Stackable {
	/**
	 * The quantity of the item.
	 *
	 * @return the item's quantity
	 */
	int stackSize();

	/**
	 * @param <T>
	 */
	interface Query<T> {
		int count();

		int count(boolean stacks);
	}
}
