package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class RSHintArrow extends ReflectProxy {
	public RSHintArrow(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getTargetID() {
		return reflector.accessInt(this);
	}

	public int getPlane() {
		return reflector.accessInt(this);
	}

	public int getX() {
		return reflector.accessInt(this);
	}

	public int getY() {
		return reflector.accessInt(this);
	}

	public int getType() {
		return reflector.accessInt(this);
	}
}
