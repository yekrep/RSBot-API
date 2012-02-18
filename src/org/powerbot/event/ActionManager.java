package org.powerbot.event;

public interface ActionManager {
	public void listen();

	public void lock();

	public void destroy();

	public void purge();
}
