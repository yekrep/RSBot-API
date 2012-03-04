package org.powerbot.game.event.listener;

import org.powerbot.game.event.MessageEvent;

import java.util.EventListener;

public interface MessageListener extends EventListener {
	public void messageReceived(final MessageEvent e);
}
