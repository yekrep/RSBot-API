package org.powerbot.bot.rt4.client;

import org.powerbot.bot.Reflector;

public class Player extends Actor {
	public Player(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getCombatLevel() {
		return reflector.accessInt(this);
	}

	public String getName() {
		return reflector.accessString(this);
	}

	public int getTeam() {
		return reflector.accessInt(this);
	}

	public PlayerComposite getComposite() {
		return new PlayerComposite(reflector, reflector.access(this));
	}
}
