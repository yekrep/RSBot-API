package org.powerbot.event;

import java.util.EventListener;

public interface MessageListener extends EventListener {
	public void messageReceived(MessageEvent e);
}
