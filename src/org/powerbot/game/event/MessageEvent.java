package org.powerbot.game.event;

import java.util.EventListener;

import org.powerbot.event.EventDispatcher;
import org.powerbot.event.GameEvent;
import org.powerbot.game.event.listener.MessageListener;

public class MessageEvent extends GameEvent {
	private static final long serialVersionUID = 1L;
	private final int id;
	private final String sender, message;

	public MessageEvent(final int id, final String sender, final String message) {
		setType(EventDispatcher.MESSAGE_EVENT);
		this.id = id;
		this.sender = sender;
		this.message = message;
	}

	@Override
	public void dispatch(final EventListener eventListener) {
		((MessageListener) eventListener).messageReceived(this);
	}

	public int getId() {
		return id;
	}

	public String getSender() {
		return sender;
	}

	public String getMessage() {
		return message;
	}
}
