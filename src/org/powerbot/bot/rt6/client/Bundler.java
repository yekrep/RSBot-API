package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class Bundler extends ReflectProxy {
	public Bundler(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public Cache getConfigCache() {
		return new Cache(reflector, reflector.access(this));
	}

	public Resources getResources() {
		return new Resources(reflector, reflector.access(this));
	}
}
