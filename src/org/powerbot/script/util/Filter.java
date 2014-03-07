package org.powerbot.script.util;

public interface Filter<T> {
	public boolean accept(final T t);
}
