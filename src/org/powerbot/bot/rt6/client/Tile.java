package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class Tile extends ReflectProxy {
	public Tile(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public ItemPile getItemPile() {
		return new ItemPile(reflector, reflector.access(this));
	}

	public Object getWallDecoration1() {
		return reflector.access(this);
	}

	public Object getWallDecoration2() {
		return reflector.access(this);
	}

	public Object getBoundary1() {
		return reflector.access(this);
	}

	public Object getBoundary2() {
		return reflector.access(this);
	}

	public Object getFloorDecoration() {
		return reflector.access(this);
	}

	public RenderableNode getInteractives() {
		return new RenderableNode(reflector, reflector.access(this));
	}
}
