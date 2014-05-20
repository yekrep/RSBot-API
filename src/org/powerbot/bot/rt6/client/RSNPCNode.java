package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class RSNPCNode extends ReflectProxy {
	public RSNPCNode(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public RSNPC getRSNPC() {
		return new RSNPC(reflector, reflector.access(this));
	}
}
