package org.powerbot.script;

import java.util.EventListener;

public interface MessageListener extends EventListener {
	void messaged(MessageEvent e);
}
