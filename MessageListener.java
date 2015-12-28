package org.powerbot.script;

import java.util.EventListener;

/**
 * MessageListener
 * A listener for chat box traffic.
 */
public interface MessageListener extends EventListener {
	void messaged(MessageEvent e);
}
