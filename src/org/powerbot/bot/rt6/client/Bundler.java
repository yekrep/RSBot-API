package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class Bundler extends ReflectProxy {
	private static final Reflector.FieldCache a = new Reflector.FieldCache(),
			b = new Reflector.FieldCache();

	public Bundler(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public Cache getConfigCache() {
		return new Cache(reflector, reflector.access(this, a));
	}

	public Resources getResources() {
		return new Resources(reflector, reflector.access(this, b));
	}
}
