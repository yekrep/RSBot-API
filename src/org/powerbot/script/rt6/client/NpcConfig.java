package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class NpcConfig extends ReflectProxy {
	private static final Reflector.FieldCache a = new Reflector.FieldCache(),
			b = new Reflector.FieldCache(),
			c = new Reflector.FieldCache(),
			d = new Reflector.FieldCache(),
			e = new Reflector.FieldCache(),
			f = new Reflector.FieldCache();

	public NpcConfig(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getId() {
		return reflector.accessInt(this, a);
	}

	public String[] getActions() {
		return reflector.access(this, b, String[].class);
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

	public short[] getOverheadArray2() {
		return reflector.access(this, f, short[].class);
	}
}
