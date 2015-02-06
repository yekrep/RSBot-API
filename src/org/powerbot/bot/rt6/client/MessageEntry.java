package org.powerbot.bot.rt6.client;

import org.powerbot.bot.Reflector;

public class MessageEntry extends NodeSub {
	public MessageEntry(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public String getSender() {
		return reflector.accessString(this);
	}

	public String getMessage() {
		return reflector.accessString(this);
	}

	public int getType() {
		return reflector.accessInt(this);
	}
}
