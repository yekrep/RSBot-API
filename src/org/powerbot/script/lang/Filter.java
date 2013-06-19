package org.powerbot.script.lang;

public interface Filter<T> {
	public boolean accept(T t);
}
