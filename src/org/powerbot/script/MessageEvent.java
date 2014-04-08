package org.powerbot.script;

import java.util.EventListener;

import org.powerbot.bot.AbstractEvent;

/**
 * A message event that is dispatched when a new message is dispatched in the game.
 */
public class MessageEvent extends AbstractEvent {
	private static final long serialVersionUID = 4178447203851407187L;
	public static final int MESSAGE_EVENT = 0x20;
	private final int id;
	private final String source, message;

	public MessageEvent(final int id, final String source, final String message) {
		super(MESSAGE_EVENT);
		this.id = id;
		this.source = source;
		this.message = message;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void call(final EventListener eventListener) {
		((MessageListener) eventListener).messaged(this);
	}

	@Deprecated
	public int getId() {
		return type();
	}

	/**
	 * @return the id of this message.
	 */
	public int type() {
		return id;
	}

	@Deprecated
	public String getSender() {
		return source;
	}

	/**
	 * @return the name of the sender of this message
	 */
	public String source() {
		return source;
	}

	@Deprecated
	public String getMessage() {
		return message;
	}

	/**
	 * @return the contents of this message
	 */
	public String text() {
		return message;
	}
}
