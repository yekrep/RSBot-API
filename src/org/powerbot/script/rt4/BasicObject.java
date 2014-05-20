package org.powerbot.script.rt4;

import java.lang.reflect.Method;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.rt4.client.BoundaryObject;
import org.powerbot.bot.rt4.client.FloorObject;
import org.powerbot.bot.rt4.client.WallObject;

class BasicObject {
	protected final ReflectProxy object;

	protected BasicObject(final ReflectProxy object) {
		this.object = object;
	}

	public BasicObject(final FloorObject object) {
		this.object = object;
	}

	public BasicObject(final WallObject object) {
		this.object = object;
	}

	public BasicObject(final BoundaryObject object) {
		this.object = object;
	}

	public int getUid() {
		final Class<?> c = object.getClass();
		try {
			final Method m = c.getMethod("getUid");
			return (Integer) m.invoke(object);
		} catch (final Exception ignored) {
		}
		return -1;
	}

	public int getMeta() {
		final Class<?> c = object.getClass();
		try {
			final Method m = c.getMethod("getUid");
			return (Integer) m.invoke(object);
		} catch (final Exception ignored) {
		}
		return -1;
	}

	public Object getObject() {
		return object.obj.get();
	}
}
