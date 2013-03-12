package org.powerbot.script;

public interface Subscribable<T> {

	public void subscribe(T e);

	public void unsubscribe(T e);
}
