package org.powerbot.bot.rt6.client;

import org.powerbot.bot.Reflector;

public class RSPlayer extends RSCharacter {
	public RSPlayer(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getTeam() {
		return reflector.accessInt(this);
	}

	public RSPlayerComposite getComposite() {
		return new RSPlayerComposite(reflector, reflector.access(this));
	}

	public String getName() {
		return reflector.accessString(this);
	}

	public int getLevel() {
		return reflector.accessInt(this);
	}

	public int[] getOverheadArray1() {
		return reflector.accessInts(this);
	}

	public int[] getOverheadArray2() {
		return reflector.accessInts(this);
	}
}
