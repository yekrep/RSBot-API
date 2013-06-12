package org.powerbot.script.lang;

import org.powerbot.script.util.Filter;
import org.powerbot.script.wrappers.Locatable;

/**
 * @author Timer
 */
public interface Filterable<T> {
	public T[] list();

	public Filterable<T> branch();

	public T nearest();

	public T nearest(Locatable loc);

	public Filterable<T> filter(Filter<T> filter);

	public Filterable<T> range(int range);

	public Filterable<T> range(int range, Locatable loc);

	public Filterable<T> at(Locatable locatable);

	public Filterable<T> id(int... ids);

	public boolean accept(Filter<T> filter);

	public boolean contains(T obj);
}
