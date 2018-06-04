package org.powerbot.script.rt4;

import org.powerbot.bot.ReflectProxy;

import java.lang.reflect.Method;

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
			final Object v = m.invoke(object);
			if (v instanceof Integer) {
				return (int) v;
			}
			final long l = (long) v;
			final int x = (int) l & 0x7f, z = (int) ((l >> 7) & 0x7f), i = (int) (l >> 17);
			return i << 14 | z << 7 | x;
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
