package org.powerbot.bot.rt4.client;

import org.powerbot.bot.reflect.ContextAccessor;
import org.powerbot.bot.reflect.ReflectionEngine;

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
		final Object[] arr = engine.access(this, Object[].class);
		final GameObject[] arr2 = arr != null ? new GameObject[arr.length] : null;
		if (arr != null) {
			for (int i = 0; i < arr.length; i++) {
				arr2[i] = new GameObject(engine, arr[i]);
			}
		}
		return arr2;
	}

	public int getGameObjectLength() {
		return engine.accessInt(this);
	}
}
