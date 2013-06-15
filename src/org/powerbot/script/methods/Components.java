package org.powerbot.script.methods;

import org.powerbot.client.Client;
import org.powerbot.script.wrappers.Component;
import org.powerbot.script.wrappers.Widget;

class Components extends MethodProvider {
	private final Container container;

	public Components(MethodContext factory) {
		super(factory);
		this.container = new Container();
	}

	Component getCompass() {
		if (container == null) {
			return null;
		}

		container.sync();
		if (container.compass == -1) {
			final Widget game = ctx.widgets.get(container.index);
			final Component[] components = game != null ? game.getComponents() : null;
			if (components != null) {
				for (final Component c : components) {
					final String[] actions = c.getActions();
					if (actions != null && actions.length == 1 && actions[0].equalsIgnoreCase("face north")) {
						container.compass = c.getIndex();
						break;
					}
				}
			}
		}
		if (container.compass != -1) {
			return ctx.widgets.get(container.index, container.compass);
		}
		return null;
	}

	Component getMap() {
		if (container == null) {
			return null;
		}

		container.sync();
		if (container.map == -1) {
			final Widget game = ctx.widgets.get(container.index);
			final Component[] components = game != null ? game.getComponents() : null;
			if (components != null) {
				for (final Component c : components) {
					if (c.getContentType() == 1338) {
						container.map = c.getIndex();
						break;
					}
				}
			}
		}
		if (container.map != -1) {
			return ctx.widgets.get(container.index, container.map);
		}
		return null;
	}

	Component getTab(final int index) {
		if (container == null || index < 0 || index >= container.tabs.length) {
			return null;
		}

		container.sync();
		if (container.tabs[index] == -1) {
			final Widget game = ctx.widgets.get(container.index);
			final Component[] components = game != null ? game.getComponents() : null;
			if (components != null) {
				for (final Component c : components) {
					final String[] actions = c.getActions();
					if (actions != null && actions.length > 0 && actions[0].equalsIgnoreCase(Game.TAB_NAMES[index])) {
						container.tabs[index] = c.getIndex();
						break;
					}
				}
			}
		}
		if (container.tabs[index] != -1) {
			return ctx.widgets.get(container.index, container.tabs[index]);
		}
		return null;
	}

	private final class Container {
		private Client client;
		private int index;
		private int compass, map;
		private int[] tabs;

		private Container() {
			index = 0;
			sync();
		}

		private void sync() {
			Client client = ctx.getClient();
			int index;
			if (client == null || this.client != client) {
				index = -1;
			} else {
				index = client.getGUIRSInterfaceIndex();
			}
			this.client = client;
			if (index != this.index) {
				this.index = index;
				this.compass = -1;
				this.map = -1;
				this.tabs = new int[Game.TAB_NAMES.length];
				for (int i = 0; i < this.tabs.length; i++) {
					this.tabs[i] = -1;
				}
			}
		}
	}
}