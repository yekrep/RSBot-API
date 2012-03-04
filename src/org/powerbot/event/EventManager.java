package org.powerbot.event;

import java.util.EventListener;
import java.util.EventObject;

public interface EventManager {
	public void dispatch(EventObject event);

	public abstract void fire(EventObject eventObject);

	public void accept(EventListener eventListener);

	public void remove(EventListener eventListener);

	public void setActive(boolean active);
}
