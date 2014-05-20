package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.Reflector;

public class RSNPCDef extends ContextAccessor {
	public RSNPCDef(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getID() {
		return engine.accessInt(this);
	}

	public String[] getActions() {
		return engine.access(this, String[].class);
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

	public short[] getOverheadArray2() {
		return engine.access(this, short[].class);
	}
}
