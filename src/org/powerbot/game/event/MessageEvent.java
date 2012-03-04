package org.powerbot.game.event;

import org.powerbot.event.EventDispatcher;
import org.powerbot.event.GameEvent;
import org.powerbot.game.event.listener.MessageListener;

import java.util.EventListener;

public class MessageEvent extends GameEvent {
	private final int id;
	private final String sender, message;

	public MessageEvent(final int id, final String sender, final String message) {
		setType(EventDispatcher.MESSAGE_EVENT);
		this.id = id;
		this.sender = sender;
		this.message = message;
	}

	@Override
	public void dispatch(EventListener eventListener) {
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
