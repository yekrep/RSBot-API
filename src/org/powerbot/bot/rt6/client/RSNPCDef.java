package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class RSNPCDef extends ReflectProxy {
	public RSNPCDef(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getID() {
		return reflector.accessInt(this);
	}

	public String[] getActions() {
		return reflector.access(this, String[].class);
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

	public short[] getOverheadArray2() {
		return reflector.access(this, short[].class);
	}
}
