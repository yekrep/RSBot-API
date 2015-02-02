package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class RenderableNode extends ReflectProxy {
	public RenderableNode(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public RenderableNode getNext() {
		return new RenderableNode(reflector, reflector.access(this));
	}

	public RenderableEntity getEntity() {
		return new RenderableEntity(reflector, reflector.access(this));
	}
}
