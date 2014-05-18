package org.powerbot.bot.rt4.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.ReflectionEngine;

public class Tile extends ContextAccessor {
	public Tile(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public ItemPile getItemPile() {
		return new ItemPile(engine, engine.access(this));
	}

	public BoundaryObject getBoundaryObject() {
		return new BoundaryObject(engine, engine.access(this));
	}

	public WallObject getWallObject() {
		return new WallObject(engine, engine.access(this));
	}

	public FloorObject getFloorObject() {
		return new FloorObject(engine, engine.access(this));
	}

	public GameObject[] getGameObjects() {
		return engine.access(this, GameObject[].class);
	}

	public int getGameObjectLength() {
		return engine.accessInt(this);
	}
}
