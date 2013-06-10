package org.powerbot.script.methods;

import org.powerbot.client.Client;
import org.powerbot.script.wrappers.Component;
import org.powerbot.script.wrappers.Widget;

public class Components {
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
		ClientFactory clientFactory = ClientFactory.getFactory();
		Container c = clientFactory.components;
		if (c == null) {
			c = new Container();
			clientFactory.components = c;
		}
		c.sync();
		return c.index != -1 ? c : null;
	}

	public static final class Container {
		private Client client;
		private int index;
		private int compass, map;
		private int[] tabs;

		private Container() {
			index = 0;
			sync();
		}

		private void sync() {
			Client client = ClientFactory.getFactory().getClient();
			int index;
			if (client != this.client) index = -1;
			else index = client.getGUIRSInterfaceIndex();
			this.client = client;
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
