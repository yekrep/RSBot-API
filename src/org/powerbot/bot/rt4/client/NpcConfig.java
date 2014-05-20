package org.powerbot.bot.rt4.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class NpcConfig extends ReflectProxy {
	public NpcConfig(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public int getId() {
		return reflector.accessInt(this);
	}

	public int getLevel() {
		return reflector.accessInt(this);
	}

	public String getName() {
		return reflector.accessString(this);
	}

	public String[] getActions() {
		return reflector.access(this, String[].class);
	}

	public int[] getConfigs() {
		return reflector.accessInts(this);
	}

	public int getVarpbitIndex() {
		return reflector.accessInt(this);
	}

	public int getVarbit() {
		return reflector.accessInt(this);
	}
}
