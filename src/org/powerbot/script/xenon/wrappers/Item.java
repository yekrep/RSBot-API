package org.powerbot.script.xenon.wrappers;

import org.powerbot.game.client.RSItem;

public class Item {//TODO complete, equals
	private final int id, stackSize;
	private final Component component;

	public Item(final int id, final int stackSize) {
		this.id = id;
		this.stackSize = stackSize;
		this.component = null;
	}

	public Item(final RSItem item) {
		this.id = item.getId();
		this.stackSize = item.getStackSize();
		this.component = null;
	}

	public Item(final Component component) {
		this.id = component.getItemId();
		this.stackSize = component.getItemStackSize();
		this.component = component;
	}

	public boolean equals(final Object o) {
		if (o == null || !(o instanceof Item)) return false;
		final Item i = (Item) o;
		return this.id == i.id && this.stackSize == i.stackSize &&
				(this.component == null || this.component.equals(i.component));
	}
}
