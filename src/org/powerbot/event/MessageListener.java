package org.powerbot.event;

import java.util.EventListener;

import org.powerbot.bot.event.MessageEvent;

public interface MessageListener extends EventListener {
	public void messaged(MessageEvent e);
}
