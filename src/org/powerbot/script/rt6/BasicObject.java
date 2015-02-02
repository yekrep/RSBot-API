package org.powerbot.script.rt6;

import java.lang.reflect.Method;

import org.powerbot.bot.ReflectProxy;

class BasicObject {
	protected final ReflectProxy object;
	private final int floor;

	protected BasicObject(final ReflectProxy object, final int floor) {
		this.object = object;
		this.floor = floor;
	}

	public int getId() {
		final Class<?> c = object.getClass();
		try {
			final Method m = c.getMethod("getId");
			return (Integer) m.invoke(object);
		} catch (final Exception ignored) {
		}
		return -1;
	}

	public int getFloor() {
		return floor;
	}

	public Object getObject() {
		return object.obj.get();
	}
}
