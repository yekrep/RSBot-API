package org.powerbot.script.rt6;

import java.util.EventListener;

import org.powerbot.bot.rt6.activation.MessageEvent;

public interface MessageListener extends EventListener {
	public void messaged(MessageEvent e);
}
