package org.powerbot.bot.rt6.client;

import org.powerbot.bot.Reflector;

public class Npc extends Actor {
	private static final Reflector.FieldCache a = new Reflector.FieldCache(),
			b = new Reflector.FieldCache();

	public Npc(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public NpcConfig getConfig() {
		return new NpcConfig(reflector, reflector.access(this, a));
	}

	public OverheadSprites getOverhead() {
		return new OverheadSprites(reflector, reflector.access(this, b));
	}
}
