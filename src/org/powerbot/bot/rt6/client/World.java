package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class World extends ReflectProxy {
	private static final Reflector.FieldCache a = new Reflector.FieldCache(),
			b = new Reflector.FieldCache(),
			c = new Reflector.FieldCache(),
			d = new Reflector.FieldCache();

	public World(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public FloorSettings getFloorSettings() {
		return new FloorSettings(reflector, reflector.access(this, a));
	}

	public MapOffset getMapOffset() {
		return new MapOffset(reflector, reflector.access(this, b));
	}

	public Landscape getLandscape() {
		return new Landscape(reflector, reflector.access(this, c));
	}

	public Bundler getSceneryBundle() {
		return new Bundler(reflector, reflector.access(this, d));
	}
}
