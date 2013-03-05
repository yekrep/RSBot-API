package org.powerbot.core.event.events;

@Deprecated
public class MessageEvent extends org.powerbot.script.event.MessageEvent {
	public MessageEvent(int id, String sender, String message) {
		super(id, sender, message);
	}
}
