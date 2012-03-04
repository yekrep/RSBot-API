package org.powerbot.game.event.listener;

import java.util.EventListener;

import org.powerbot.game.event.MessageEvent;

public interface MessageListener extends EventListener {
	public void messageReceived(final MessageEvent e);
}
