package org.powerbot.bot.rt4.client;

import org.powerbot.bot.reflect.ReflectionEngine;

public class GameObject extends BasicObject {
	public GameObject(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public int getX() {
		return engine.accessInt(this);
	}

	public int getZ() {
		return engine.accessInt(this);
	}

	public int getX1() {
		return engine.accessInt(this);
	}

	public int getY1() {
		return engine.accessInt(this);
	}

	public int getX2() {
		return engine.accessInt(this);
	}

	public int getY2() {
		return engine.accessInt(this);
	}
}
