package org.powerbot.bot.rt4.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class Tile extends ReflectProxy {
	public Tile(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public ItemPile getItemPile() {
		return new ItemPile(reflector, reflector.access(this));
	}

	public BoundaryObject getBoundaryObject() {
		return new BoundaryObject(reflector, reflector.access(this));
	}

	public WallObject getWallObject() {
		return new WallObject(reflector, reflector.access(this));
	}

	public FloorObject getFloorObject() {
		return new FloorObject(reflector, reflector.access(this));
	}

	public GameObject[] getGameObjects() {
		final Object[] arr = reflector.access(this, Object[].class);
		final GameObject[] arr2 = arr != null ? new GameObject[arr.length] : null;
		if (arr != null) {
			for (int i = 0; i < arr.length; i++) {
				arr2[i] = new GameObject(reflector, arr[i]);
			}
		}
		return arr2;
	}

	public int getGameObjectLength() {
		return reflector.accessInt(this);
	}
}
