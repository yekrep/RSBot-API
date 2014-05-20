package org.powerbot.bot.rt6.client;

import org.powerbot.bot.Reflector;

public class RSPlayer extends RSCharacter {
	public RSPlayer(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getTeam() {
		return engine.accessInt(this);
	}

	public RSPlayerComposite getComposite() {
		return new RSPlayerComposite(engine, engine.access(this));
	}

	public String getName() {
		return engine.accessString(this);
	}

	public int getLevel() {
		return engine.accessInt(this);
	}

	public int[] getOverheadArray1() {
		return engine.accessInts(this);
	}

	public int[] getOverheadArray2() {
		return engine.accessInts(this);
	}
}
