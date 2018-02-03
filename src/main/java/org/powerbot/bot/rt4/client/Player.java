package org.powerbot.bot.rt4.client;

import org.powerbot.bot.Reflector;

public class Player extends Actor {
	private static final Reflector.FieldCache a = new Reflector.FieldCache(),
			b = new Reflector.FieldCache(),
			c = new Reflector.FieldCache(),
			d = new Reflector.FieldCache();

	public Player(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getCombatLevel() {
		return reflector.accessInt(this, a);
	}

	public String getName() {
		return new StringRecord(reflector, reflector.access(this, b)).getValue();
	}

	public int getTeam() {
		return reflector.accessInt(this, c);
	}

	public PlayerComposite getComposite() {
		return new PlayerComposite(reflector, reflector.access(this, d));
	}
}
