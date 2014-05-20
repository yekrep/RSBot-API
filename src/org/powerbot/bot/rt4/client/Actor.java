package org.powerbot.bot.rt4.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class Actor extends ReflectProxy {
	public Actor(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getX() {
		return reflector.accessInt(this);
	}

	public int getZ() {
		return reflector.accessInt(this);
	}

	public int getHeight() {
		return reflector.accessInt(this);
	}

	public int getAnimation() {
		return reflector.accessInt(this);
	}

	public int getSpeed() {
		return reflector.accessInt(this);
	}

	public int getCurrentHealth() {
		return reflector.accessInt(this);
	}

	public int getMaxHealth() {
		return reflector.accessInt(this);
	}

	public int getCycleEnd() {
		return reflector.accessInt(this);
	}

	public String getOverheadMessage() {
		return reflector.accessString(this);
	}

	public int getOrientation() {
		return reflector.accessInt(this);
	}

	public int getInteractingIndex() {
		return reflector.accessInt(this);
	}
}
