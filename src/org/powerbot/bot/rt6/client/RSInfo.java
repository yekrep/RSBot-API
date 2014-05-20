package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.Reflector;

public class RSInfo extends ContextAccessor {
	public RSInfo(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public RSGroundBytes getGroundBytes() {
		return new RSGroundBytes(engine, engine.access(this));
	}

	public BaseInfo getBaseInfo() {
		return new BaseInfo(engine, engine.access(this));
	}

	public RSGroundInfo getRSGroundInfo() {
		return new RSGroundInfo(engine, engine.access(this));
	}

	public RSObjectDefLoader getRSObjectDefLoaders() {
		return new RSObjectDefLoader(engine, engine.access(this));
	}
}
