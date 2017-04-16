package org.powerbot.bot.rt6.client;

import org.powerbot.bot.Reflector;

public class MessageEntry extends NodeSub {
	private static final Reflector.FieldCache a = new Reflector.FieldCache(),
			b = new Reflector.FieldCache(),
			c = new Reflector.FieldCache();

	public MessageEntry(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public String getSender() {
		return reflector.accessString(this, a);
	}

	public String getMessage() {
		return reflector.accessString(this, b);
	}

	public int getType() {
		return reflector.accessInt(this, c);
	}
}
