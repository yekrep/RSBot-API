package org.powerbot.script.rt4;

import java.lang.reflect.Method;

import org.powerbot.bot.ReflectProxy;

/**
 * BasicObject
 * An object representing an internal game object.
 *
 * @see GameObject
 */
public class BasicObject {
	protected final ReflectProxy object;

	public BasicObject(final ReflectProxy object) {
		this.object = object;
	}

	boolean isComplex() {
		final Class<?> c = object.getClass();
		try {
			return c.getDeclaredMethod("getX") != null;
		} catch (final NoSuchMethodException ignored) {
		}
		return false;
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
			final Method m = c.getMethod("getMeta");
			return (Integer) m.invoke(object);
		} catch (final Exception ignored) {
		}
		return -1;
	}

	public int getX() {
		final Class<?> c = object.getClass();
		try {
			final Method m = c.getMethod("getX");
			return (Integer) m.invoke(object);
		} catch (final Exception ignored) {
		}
		return -1;
	}

	public int getZ() {
		final Class<?> c = object.getClass();
		try {
			final Method m = c.getMethod("getZ");
			return (Integer) m.invoke(object);
		} catch (final Exception ignored) {
		}
		return -1;
	}

	public int getX1() {
		final Class<?> c = object.getClass();
		try {
			final Method m = c.getMethod("getX1");
			return (Integer) m.invoke(object);
		} catch (final Exception ignored) {
		}
		return -1;
	}

	public int getY1() {
		final Class<?> c = object.getClass();
		try {
			final Method m = c.getMethod("getY1");
			return (Integer) m.invoke(object);
		} catch (final Exception ignored) {
		}
		return -1;
	}

	public int getX2() {
		final Class<?> c = object.getClass();
		try {
			final Method m = c.getMethod("getX2");
			return (Integer) m.invoke(object);
		} catch (final Exception ignored) {
		}
		return -1;
	}

	public int getY2() {
		final Class<?> c = object.getClass();
		try {
			final Method m = c.getMethod("getY2");
			return (Integer) m.invoke(object);
		} catch (final Exception ignored) {
		}
		return -1;
	}

	@Override
	public int hashCode() {
		return object.hashCode();
	}

	@Override
	public boolean equals(final Object o) {
		return o instanceof BasicObject && object.equals(((BasicObject) o).object);
	}
}
