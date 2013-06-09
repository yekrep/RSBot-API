package org.powerbot.nscript.lang;

public interface Subscribable<T> {
	public void subscribe(T t);

	public void unsubscribe(T t);
}
