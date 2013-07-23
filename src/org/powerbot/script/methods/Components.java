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

	private final class Container {
		private Client client;
		private int index;
		private int map;

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
				this.map = -1;
			}
		}
	}
}