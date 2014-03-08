package org.powerbot.script;

public interface Filter<T> {
	public boolean accept(T t);
}
