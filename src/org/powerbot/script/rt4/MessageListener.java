package org.powerbot.script.rt4;

import java.util.EventListener;

import org.powerbot.bot.rt4.activation.MessageEvent;

public interface MessageListener extends EventListener {
	public void messaged(MessageEvent e);
}
