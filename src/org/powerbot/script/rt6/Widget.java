package org.powerbot.script.rt6;

import java.util.Arrays;
import java.util.Iterator;

import org.powerbot.bot.rt6.client.Client;
import org.powerbot.bot.rt6.client.ComponentContainer;
import org.powerbot.script.Identifiable;
import org.powerbot.script.Validatable;

public class Widget extends ClientAccessor implements Identifiable, Validatable, Iterable<Component> {
	private final int index;
	private final Object LOCK;
	private Component[] cache;

	public Widget(final ClientContext ctx, final int index) {
		super(ctx);
		this.index = index;
		LOCK = new Object();
		cache = new Component[0];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int id() {
		return index;
	}

	public int componentCount() {
		final org.powerbot.bot.rt6.client.Widget[] internal = getInternalComponents();
		return internal != null ? internal.length : 0;
	}

	public Component[] components() {
		synchronized (LOCK) {
			final org.powerbot.bot.rt6.client.Widget[] components = getInternalComponents();
			if (components == null) {
				return cache;
			}
			if (cache.length < components.length) {
				final int len = cache.length;
				cache = Arrays.copyOf(cache, components.length);
				for (int i = len; i < components.length; i++) {
					cache[i] = new Component(ctx, this, i);
				}
			}
			return cache.clone();
		}
	}

	public Component component(final int index) {
		synchronized (LOCK) {
			if (index < cache.length) {
				return cache[index];
			}
			final org.powerbot.bot.rt6.client.Widget[] components = getInternalComponents();
			final int mod = Math.max(components != null ? components.length : 0, index + 1);
			if (cache.length < mod) {
				final int len = cache.length;
				cache = Arrays.copyOf(cache, mod);
				for (int i = len; i < mod; i++) {
					cache[i] = new Component(ctx, this, i);
				}
			}
			return cache[index];
		}
	}

	@Override
	public boolean valid() {
		final Client client = ctx.client();
		if (client == null || index < 0) {
			return false;
		}

		final ComponentContainer[] containers = client.getWidgets();
		return containers != null && index < containers.length && containers[index] != null && containers[index].getComponents() != null;
	}

	org.powerbot.bot.rt6.client.Widget[] getInternalComponents() {
		final Client client = ctx.client();
		if (client == null) {
			return null;
		}
		final ComponentContainer[] containers = client.getWidgets();
		final ComponentContainer container;
		if (containers != null && index >= 0 && index < containers.length && (container = containers[index]) != null) {
			return container.getComponents();
		}
		return null;
	}

	@Override
	public Iterator<Component> iterator() {
		return new Iterator<Component>() {
			private int nextId = 0;

			@Override
			public boolean hasNext() {
				final int count = componentCount();
				return nextId < count && valid();
			}

			@Override
			public Component next() {
				return component(nextId++);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + index + "]";
	}

	@Override
	public int hashCode() {
		return index;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || !(o instanceof Widget)) {
			return false;
		}
		final Widget w = (Widget) o;
		return w.index == index;
	}
}
