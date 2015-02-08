package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class GameLocation extends ReflectProxy {
	private static final Reflector.FieldCache a = new Reflector.FieldCache();

	public GameLocation(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public RelativePosition getRelativePosition() {
		return new RelativePosition(reflector, reflector.access(this, a));
	}
}
