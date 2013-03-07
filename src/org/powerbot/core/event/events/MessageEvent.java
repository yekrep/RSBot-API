package org.powerbot.core.event.events;

import java.util.EventListener;

import org.powerbot.core.event.listeners.MessageListener;

@Deprecated
public class MessageEvent extends org.powerbot.script.event.MessageEvent {
	public MessageEvent(int id, String sender, String message) {
		super(id, sender, message);
	}

	@Override
	public void dispatch(final EventListener eventListener) {
		((MessageListener) eventListener).messageReceived(this);
	}
}
