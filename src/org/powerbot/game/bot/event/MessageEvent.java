package org.powerbot.game.bot.event;

@Deprecated
public class MessageEvent extends org.powerbot.core.event.events.MessageEvent {
	private static final long serialVersionUID = -588778956320827021L;

	public MessageEvent(final int id, final String sender, final String message) {
		super(id, sender, message);
	}
}
