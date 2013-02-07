package org.powerbot.core.script.util;

public interface Filter<T> {
	public boolean accept(T t);
}
