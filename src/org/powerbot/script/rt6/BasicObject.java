package org.powerbot.script.rt6;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.powerbot.bot.rt6.client.AnimationBridge;
import org.powerbot.bot.rt6.client.Animator;
import org.powerbot.bot.rt6.client.RenderableEntity;

/**
 * BasicObject
 */
public class BasicObject {
	protected final RenderableEntity object;
	private final int floor;

	public BasicObject(final RenderableEntity object, final int floor) {
		this.object = object;
		this.floor = floor;
	}

	private boolean isDynamic() {
		final Class<?> c = object.getClass();
		try {
			return c.getDeclaredMethod("getBridge") != null;
		} catch (final NoSuchMethodException ignored) {
		}
		return false;
	}

	private AnimationBridge d() {
		final Class<?> c = object.getClass();
		try {
			final Method m = c.getMethod("getBridge");
			return (AnimationBridge) m.invoke(object);
		} catch (final NoSuchMethodException ignored) {
		} catch (final InvocationTargetException ignored) {
		} catch (final IllegalAccessException ignored) {
		}
		return null;
	}

	public int getId() {
		if (isDynamic()) {
			final AnimationBridge bridge = d();
			final int vid = bridge.getVariableId();
			if (vid != -1) {
				return vid;
			}
			return bridge.getId();
		}
		final Class<?> c = object.getClass();
		try {
			final Method m = c.getMethod("getId");
			return (Integer) m.invoke(object);
		} catch (final Exception ignored) {
		}
		return -1;
	}

	public int getMainId() {
		if (isDynamic()) {
			final AnimationBridge bridge = d();
			return bridge.getId();
		}
		final Class<?> c = object.getClass();
		try {
			final Method m = c.getMethod("getId");
			return (Integer) m.invoke(object);
		} catch (final Exception ignored) {
		}
		return -1;
	}

	public Animator getAnimator() {
		if (isDynamic()) {
			return d().getAnimator();
		}
		return new Animator(object.reflector, null);
	}

	public int getOrientation() {
		if (isDynamic()) {
			return d().getOrientation();
		}
		final Class<?> c = object.getClass();
		try {
			final Method m = c.getMethod("getOrientation");
			return (Byte) m.invoke(object);
		} catch (final Exception ignored) {
		}
		return -1;
	}

	public int getType() {
		if (isDynamic()) {
			return d().getType();//TODO: decode
		}
		final Class<?> c = object.getClass();
		try {
			final Method m = c.getMethod("getType");
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

	@Override
	public int hashCode() {
		return object.hashCode();
	}

	@Override
	public boolean equals(final Object o) {
		return o instanceof BasicObject && object.equals(((BasicObject) o).object);
	}
}
