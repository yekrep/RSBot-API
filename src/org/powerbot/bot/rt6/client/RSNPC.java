package org.powerbot.bot.rt6.client;

import org.powerbot.bot.Reflector;

public class RSNPC extends RSCharacter {
	public RSNPC(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public RSNPCDef getRSNPCDef() {
		return new RSNPCDef(engine, engine.access(this));
	}

	public OverheadSprites getOverhead() {
		return new OverheadSprites(engine, engine.access(this));
	}
}
