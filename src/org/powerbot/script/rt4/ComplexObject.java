package org.powerbot.script.rt4;

import org.powerbot.bot.rt4.client.GameObject;

public class ComplexObject extends BasicObject {
	public ComplexObject(final org.powerbot.bot.rt4.client.GameObject o) {
		super(o);
	}

	public org.powerbot.bot.rt4.client.GameObject getGameObject() {
		return (GameObject) object;
	}
}
