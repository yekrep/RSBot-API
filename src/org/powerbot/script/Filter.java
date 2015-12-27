package org.powerbot.script;

public interface Filter<T> {
	boolean accept(T t);
}
