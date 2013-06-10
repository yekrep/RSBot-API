package org.powerbot.script.wrappers;

import java.util.Arrays;
import java.util.Iterator;

import org.powerbot.script.methods.ClientFactory;
import org.powerbot.client.Client;
import org.powerbot.client.RSInterface;
import org.powerbot.client.RSInterfaceBase;

public class Widget implements Validatable, Iterable<Component> {
	private final int index;
	private final Object LOCK;
	private Component[] cache;

	public Widget(final int index) {
		this.index = index;
		this.LOCK = new Object();
		this.cache = new Component[0];
	}

	public int getIndex() {
		return this.index;
	}

	public int getComponentCount() {
		final RSInterface[] internal = getInternalComponents();
		return internal != null ? internal.length : 0;
	}

	public Component[] getComponents() {
		synchronized (LOCK) {
			final RSInterface[] components = getInternalComponents();
			if (components == null) return cache;
			if (cache.length < components.length) {
				final int len = cache.length;
				cache = Arrays.copyOf(cache, components.length);
				for (int i = len; i < components.length; i++) cache[i] = new Component(this, i);
			}
			return cache.clone();
		}
	}

	public Component getComponent(final int index) {
		synchronized (LOCK) {
			if (index < cache.length) return cache[index];
			final RSInterface[] components = getInternalComponents();
			final int mod = Math.max(components != null ? components.length : 0, index + 1);
			if (cache.length < mod) {
				final int len = cache.length;
				cache = Arrays.copyOf(cache, mod);
				for (int i = len; i < mod; i++) cache[i] = new Component(this, i);
			}
			return cache[index];
		}
	}

	public boolean isValid() {
		final Client client = ClientFactory.getFactory().getClient();
		if (client == null) return false;

		final RSInterfaceBase[] containers = client.getRSInterfaceCache();
		return containers != null && index < containers.length && containers[index] != null && containers[index].getComponents() != null;
	}

	RSInterface[] getInternalComponents() {
		final Client client = ClientFactory.getFactory().getClient();
		if (client == null) return null;
		final RSInterfaceBase[] containers = client.getRSInterfaceCache();
		final RSInterfaceBase container;
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
				final int count = getComponentCount();
				return nextId < count && isValid();
			}

			@Override
			public Component next() {
				return getComponent(nextId++);
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
	public boolean equals(final Object o) {
		if (o == null || !(o instanceof Widget)) return false;
		final Widget w = (Widget) o;
		return w.index == this.index;
	}
}
