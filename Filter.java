package org.powerbot.script;

/**
 * Filter
 * An object used to test objects for compliance against a defined set of rules.
 *
 * @param <T> the type of object to check against
 */
public interface Filter<T> {
	/**
	 * Determines if the item satisfies the rules of this filter.
	 *
	 * @param t the item to check against
	 * @return {@code true} is the item meets all rules of this filter; {@code false} otherwise.
	 */
	boolean accept(T t);
}
