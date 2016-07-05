package org.powerbot.bot.rt4.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class Tile extends ReflectProxy {
	private static final Reflector.FieldCache a = new Reflector.FieldCache(),
			b = new Reflector.FieldCache(),
			c = new Reflector.FieldCache(),
			d = new Reflector.FieldCache(),
			e = new Reflector.FieldCache(),
			f = new Reflector.FieldCache();

	public Tile(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public ItemPile getItemPile() {
		return new ItemPile(reflector, reflector.access(this, a));
	}

	public BoundaryObject getBoundaryObject() {
		return new BoundaryObject(reflector, reflector.access(this, b));
	}

	public WallObject getWallObject() {
		return new WallObject(reflector, reflector.access(this, c));
	}

	public FloorObject getFloorObject() {
		return new FloorObject(reflector, reflector.access(this, d));
	}

	public GameObject[] getGameObjects() {
		final Object[] arr = reflector.access(this, e, Object[].class);
		final GameObject[] arr2 = arr != null ? new GameObject[arr.length] : null;
		if (arr != null) {
			for (int i = 0; i < arr.length; i++) {
				arr2[i] = new GameObject(reflector, arr[i]);
			}
		} else {
			return new GameObject[0];
		}
		return arr2;
	}

	public int getGameObjectLength() {
		return reflector.accessInt(this, f);
	}
}
