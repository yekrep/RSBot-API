package org.powerbot.os.api.util;

public interface Filter<T> {
	public boolean accept(final T t);
}
