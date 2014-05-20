package org.powerbot.bot.rt4.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.Reflector;

public class Actor extends ContextAccessor {
	public Actor(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getX() {
		return engine.accessInt(this);
	}

	public int getZ() {
		return engine.accessInt(this);
	}

	public int getHeight() {
		return engine.accessInt(this);
	}

	public int getAnimation() {
		return engine.accessInt(this);
	}

	public int getSpeed() {
		return engine.accessInt(this);
	}

	public int getCurrentHealth() {
		return engine.accessInt(this);
	}

	public int getMaxHealth() {
		return engine.accessInt(this);
	}

	public int getCycleEnd() {
		return engine.accessInt(this);
	}

	public String getOverheadMessage() {
		return engine.accessString(this);
	}

	public int getOrientation() {
		return engine.accessInt(this);
	}

	public int getInteractingIndex() {
		return engine.accessInt(this);
	}
}
