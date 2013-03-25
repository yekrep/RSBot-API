package org.powerbot.event;

import java.util.EventListener;

/**
 * An interface that represents a class object that listens for messages.
 *
 * @author Timer
 */
public interface MessageListener extends EventListener {
	public void messageReceived(MessageEvent e);
}
