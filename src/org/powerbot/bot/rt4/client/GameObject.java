package org.powerbot.bot.rt4.client;

import org.powerbot.bot.Reflector;

public class GameObject extends BasicObject {
	public GameObject(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getX() {
		return reflector.accessInt(this);
	}

	public int getZ() {
		return reflector.accessInt(this);
	}

	public int getX1() {
		return reflector.accessInt(this);
	}

	public int getY1() {
		return reflector.accessInt(this);
	}

	public int getX2() {
		return reflector.accessInt(this);
	}

	public int getY2() {
		return reflector.accessInt(this);
	}
}
