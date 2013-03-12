package org.powerbot.script.util;

public interface Subscribable<T> {

	public void subscribe(T e);

	public void unsubscribe(T e);
}
