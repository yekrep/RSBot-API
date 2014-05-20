package org.powerbot.bot.rt6.client;

import org.powerbot.bot.reflect.ReflectionEngine;

public class RSAnimable extends RSInteractable {
	public RSAnimable(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public short getX1() {
		return engine.accessShort(this);
	}

	public short getX2() {
		return engine.accessShort(this);
	}

	public short getY1() {
		return engine.accessShort(this);
	}

	public short getY2() {
		return engine.accessShort(this);
	}
}
