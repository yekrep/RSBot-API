package org.powerbot.bot.rt6.client;

import org.powerbot.bot.Reflector;

public class RSAnimable extends RSInteractable {
	public RSAnimable(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public short getX1() {
		return reflector.accessShort(this);
	}

	public short getX2() {
		return reflector.accessShort(this);
	}

	public short getY1() {
		return reflector.accessShort(this);
	}

	public short getY2() {
		return reflector.accessShort(this);
	}
}
