package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class RSInfo extends ReflectProxy {
	public RSInfo(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public RSGroundBytes getGroundBytes() {
		return new RSGroundBytes(reflector, reflector.access(this));
	}

	public BaseInfo getBaseInfo() {
		return new BaseInfo(reflector, reflector.access(this));
	}

	public RSGroundInfo getRSGroundInfo() {
		return new RSGroundInfo(reflector, reflector.access(this));
	}

	public RSObjectDefLoader getRSObjectDefLoaders() {
		return new RSObjectDefLoader(reflector, reflector.access(this));
	}
}
