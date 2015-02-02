package org.powerbot.bot.rt6.client;

import org.powerbot.bot.Reflector;

public class Player extends Actor {
	public Player(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getTeam() {
		return reflector.accessInt(this);
	}

	public PlayerComposite getComposite() {
		return new PlayerComposite(reflector, reflector.access(this));
	}

	public String getName() {
		return reflector.accessString(this);
	}

	public int getCombatLevel() {
		return reflector.accessInt(this);
	}

	public int[] getOverheadArray1() {
		return reflector.accessInts(this);
	}

	public int[] getOverheadArray2() {
		return reflector.accessInts(this);
	}
}
