package org.powerbot.script.framework;

public interface Subscribable<T> {

	public void subscribe(T e);

	public void unsubscribe(T e);
}
