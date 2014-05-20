package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.Reflector;

public class RSNPCNode extends ContextAccessor {
	public RSNPCNode(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public RSNPC getRSNPC() {
		return new RSNPC(engine, engine.access(this));
	}
}
