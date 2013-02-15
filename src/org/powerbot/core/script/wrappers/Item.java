package org.powerbot.core.script.wrappers;

import org.powerbot.game.client.RSItem;

public class Item {//TODO complete
	private final int id, stackSize;

	public Item(final int id, final int stackSize) {
		this.id = id;
		this.stackSize = stackSize;
	}

	public Item(final RSItem item) {
		this.id = item.getId();
		this.stackSize = item.getStackSize();
	}

	public Item(final Component component) {
		this.id = component.getItemId();
		this.stackSize = component.getItemStackSize();
	}
}
