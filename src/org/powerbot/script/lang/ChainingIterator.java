package org.powerbot.script.lang;

/**
 * @author Paris
 */
public interface ChainingIterator<T> {
	public boolean next(int index, T item);
}
