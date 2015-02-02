package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class World extends ReflectProxy {
	public World(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public FloorSettings getFloorSettings() {
		return new FloorSettings(reflector, reflector.access(this));
	}

	public MapOffset getMapOffset() {
		return new MapOffset(reflector, reflector.access(this));
	}

	public Landscape getLandscape() {
		return new Landscape(reflector, reflector.access(this));
	}

	public Bundler getSceneryBundle() {
		return new Bundler(reflector, reflector.access(this));
	}
}
