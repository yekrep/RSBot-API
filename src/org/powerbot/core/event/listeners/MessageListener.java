package org.powerbot.core.event.listeners;

import java.util.EventListener;

import org.powerbot.core.event.events.MessageEvent;

/**
 * An interface that represents a class object that listens for messages.
 *
 * @author Timer
 */
public interface MessageListener extends EventListener {
	public void messageReceived(MessageEvent e);
}
