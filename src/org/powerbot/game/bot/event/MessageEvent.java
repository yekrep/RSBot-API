package org.powerbot.game.bot.event;

@Deprecated
public class MessageEvent extends org.powerbot.core.event.events.MessageEvent {
	public MessageEvent(final int id, final String sender, final String message) {
		super(id, sender, message);
	}
}
