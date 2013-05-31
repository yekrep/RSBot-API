package org.powerbot.script.methods;

import java.util.HashMap;
import java.util.Map;

import org.powerbot.bot.Bot;
import org.powerbot.client.Client;
import org.powerbot.script.wrappers.Component;
import org.powerbot.script.wrappers.Widget;

class Components {
	private static final Map<Client, Container> cache = new HashMap<>();

	static Component getCompass() {
		final Container container = get();
		if (container == null) return null;

		if (container.compass == -1) {
			final Widget game = Widgets.get(container.index);
			final Component[] components = game != null ? game.getComponents() : null;
			if (components != null) for (final Component c : components) {
				final String[] actions = c.getActions();
				if (actions != null && actions.length == 1 && actions[0].equalsIgnoreCase("face north")) {
					container.compass = c.getIndex();
					break;
				}
			}
		}
		if (container.compass != -1) return Widgets.get(container.index, container.compass);
		return null;
	}

	static Component getMap() {
		final Container container = get();
		if (container == null) return null;

		if (container.map == -1) {
			final Widget game = Widgets.get(container.index);
			final Component[] components = game != null ? game.getComponents() : null;
			if (components != null) for (final Component c : components) {
				if (c.getContentType() == 1338) {
					container.map = c.getIndex();
					break;
				}
			}
		}
		if (container.map != -1) return Widgets.get(container.index, container.map);
		return null;
	}

	static Component getTab(final int index) {
		final Container container = get();
		if (container == null || index < 0 || index >= container.tabs.length) return null;

		if (container.tabs[index] == -1) {
			final Widget game = Widgets.get(container.index);
			final Component[] components = game != null ? game.getComponents() : null;
			if (components != null) for (final Component c : components) {
				final String[] actions = c.getActions();
				if (actions != null && actions.length == 1 && actions[0].equalsIgnoreCase(Game.TAB_NAMES[index])) {
					container.tabs[index] = c.getIndex();
					break;
				}
			}
		}
		if (container.tabs[index] != -1) return Widgets.get(container.index, container.tabs[index]);
		return null;
	}

	private static Container get() {
		final Client client = Bot.client();
		if (client == null) return null;
		Container c = cache.get(client);
		if (c == null) {
			c = new Container(client);
			cache.put(client, c);
		}
		c.sync(client);
		return c.index != -1 ? c : null;
	}

	private static final class Container {
		private int index;
		private int compass, map;
		private int[] tabs;

		private Container(final Client client) {
			index = 0;
			sync(client);
		}

		private void sync(final Client client) {
			int index = client.getGUIRSInterfaceIndex();
			if (index != this.index) {
				this.index = index;
				this.compass = -1;
				this.map = -1;
				this.tabs = new int[Game.TAB_NAMES.length];
				for (int i = 0; i < this.tabs.length; i++) this.tabs[i] = -1;
			}
		}
	}
}
