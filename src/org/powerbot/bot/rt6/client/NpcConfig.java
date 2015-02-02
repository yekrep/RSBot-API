package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class NpcConfig extends ReflectProxy {
	public NpcConfig(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getId() {
		return reflector.accessInt(this);
	}

	public String[] getActions() {
		return reflector.access(this, String[].class);
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

	public short[] getOverheadArray2() {
		return reflector.access(this, short[].class);
	}
}
