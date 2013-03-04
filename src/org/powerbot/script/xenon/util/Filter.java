package org.powerbot.script.xenon.util;

public interface Filter<T> {
	public boolean accept(T t);
}
