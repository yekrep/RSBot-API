package org.powerbot.script.event;

import java.util.EventListener;

public interface MessageListener extends EventListener {
	public void messaged(MessageEvent e);
}
