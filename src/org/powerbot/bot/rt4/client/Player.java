package org.powerbot.bot.rt4.client;

import org.powerbot.bot.ReflectionEngine;

public class Player extends Actor {
	public Player(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public int getCombatLevel() {
		return engine.accessInt(this);
	}

	public String getName() {
		return engine.access(this, String.class);
	}

	public int getTeam() {
		return engine.accessInt(this);
	}

	public PlayerComposite getComposite() {
		return new PlayerComposite(engine, engine.access(this));
	}
}
