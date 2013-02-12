package org.powerbot.core.script.wrappers;

import java.util.Arrays;
import java.util.Iterator;

import org.powerbot.core.Bot;
import org.powerbot.game.client.Client;
import org.powerbot.game.client.RSInterface;
import org.powerbot.game.client.RSInterfaceBase;

public class Widget implements Iterable<Component> {
	private final int index;
	private final Object LOCK;
	private Component[] cache;

	public Widget(final int index) {
		this.index = index;
		this.LOCK = new Object();
		this.cache = new Component[0];
	}

	public int getIndex() {
		return index;
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
		final Client client = Bot.client();
		if (client == null) return false;

		final RSInterfaceBase[] containers = client.getRSInterfaceCache();
		return containers != null && index < containers.length && containers[index] != null && containers[index].getComponents() != null;
	}

	RSInterface[] getInternalComponents() {
		final Client client = Bot.client();
		if (client == null) return null;
		final RSInterfaceBase[] containers = client.getRSInterfaceCache();
		if (containers != null && index >= 0 && index < containers.length) {
			final RSInterfaceBase container = containers[index];
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
}
