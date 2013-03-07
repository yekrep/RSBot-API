package org.powerbot.core.event.listeners;

import java.util.EventListener;

import org.powerbot.core.event.events.MessageEvent;

@Deprecated
public interface MessageListener extends EventListener {
	public void messageReceived(MessageEvent e);
}