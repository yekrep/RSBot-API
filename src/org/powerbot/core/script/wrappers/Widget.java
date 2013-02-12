package org.powerbot.core.script.wrappers;

import java.util.Arrays;

import org.powerbot.core.Bot;
import org.powerbot.game.client.Client;
import org.powerbot.game.client.RSInterface;
import org.powerbot.game.client.RSInterfaceBase;

public class Widget {
	private final Container container;
	private final Widget parent;
	private final int index;

	public Widget(final Container container, final int index) {
		this(container, null, index);
	}

	public Widget(final Container container, final Widget parent, final int index) {
		this.container = container;
		this.parent = parent;
		this.index = index;
	}

	private RSInterface getComponent() {
		RSInterface[] components;
		if (parent != null) {
			final RSInterface parentComponent = parent.getComponent();
			components = parentComponent != null ? parentComponent.getComponents() : null;
		} else {
			components = container.getComponents();
		}
		return components != null && index < components.length ? components[index] : null;
	}

	public static class Container {
		private final int index;
		private final Object LOCK;
		private Widget[] cache;

		public Container(final int index) {
			this.index = index;
			this.LOCK = new Object();
			this.cache = new Widget[0];
		}

		public Widget[] getWidgets() {
			synchronized (LOCK) {
				final RSInterface[] components = getComponents();
				if (components == null) return cache;
				if (cache.length < components.length) {
					final int len = cache.length;
					cache = Arrays.copyOf(cache, components.length);
					for (int i = len; i < components.length; i++) cache[i] = new Widget(this, i);
				}
				return cache.clone();
			}
		}

		public Widget getWidget(final int index) {
			synchronized (LOCK) {
				if (index < cache.length) return cache[index];
				final RSInterface[] components = getComponents();
				final int mod = Math.max(components != null ? components.length : 0, index + 1);
				if (cache.length < mod) {
					final int len = cache.length;
					cache = Arrays.copyOf(cache, mod);
					for (int i = len; i < mod; i++) cache[i] = new Widget(this, i);
				}
				return cache[index];
			}
		}

		private RSInterface[] getComponents() {
			final Client client = Bot.client();
			if (client == null) return null;
			final RSInterfaceBase[] containers = client.getRSInterfaceCache();
			if (containers != null && index >= 0 && index < containers.length) {
				final RSInterfaceBase container = containers[index];
				return container.getComponents();
			}
			return null;
		}
	}
}
