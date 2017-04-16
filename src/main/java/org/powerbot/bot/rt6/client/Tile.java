package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class Tile extends ReflectProxy {
	private static final Reflector.FieldCache a = new Reflector.FieldCache(),
			b = new Reflector.FieldCache(),
			c = new Reflector.FieldCache(),
			d = new Reflector.FieldCache(),
			e = new Reflector.FieldCache(),
			f = new Reflector.FieldCache(),
			g = new Reflector.FieldCache();

	public Tile(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public ItemPile getItemPile() {
		return new ItemPile(reflector, reflector.access(this, a));
	}

	public Object getWallDecoration1() {
		return reflector.access(this, b);
	}

	public Object getWallDecoration2() {
		return reflector.access(this, c);
	}

	public Object getBoundary1() {
		return reflector.access(this, d);
	}

	public Object getBoundary2() {
		return reflector.access(this, e);
	}

	public Object getFloorDecoration() {
		return reflector.access(this, f);
	}

	public RenderableNode getInteractives() {
		return new RenderableNode(reflector, reflector.access(this, g));
	}
}
