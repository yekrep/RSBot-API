package org.powerbot.game.event.listener;

import java.util.EventListener;

import org.powerbot.game.event.MessageEvent;

/**
 * An interface that represents a class object that listens for messages.
 *
 * @author Timer
 */
public interface MessageListener extends EventListener {
	public void messageReceived(final MessageEvent e);
}
