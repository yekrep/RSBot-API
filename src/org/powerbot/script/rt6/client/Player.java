package org.powerbot.bot.rt6.client;

import org.powerbot.bot.Reflector;

public class Player extends Actor {
	private static final Reflector.FieldCache a = new Reflector.FieldCache(),
			b = new Reflector.FieldCache(),
			c = new Reflector.FieldCache(),
			d = new Reflector.FieldCache(),
			e = new Reflector.FieldCache(),
			f = new Reflector.FieldCache();

	public Player(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getTeam() {
		return reflector.accessInt(this, a);
	}

	public PlayerComposite getComposite() {
		return new PlayerComposite(reflector, reflector.access(this, b));
	}

	public String getName() {
		return reflector.accessString(this, c);
	}

	public int getCombatLevel() {
		return reflector.accessInt(this, d);
	}

	public int[] getOverheadArray1() {
		return reflector.accessInts(this, e);
	}

	public int[] getOverheadArray2() {
		return reflector.accessInts(this, f);
	}
}
