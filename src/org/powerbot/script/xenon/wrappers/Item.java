package org.powerbot.script.xenon.wrappers;

import org.powerbot.bot.Bot;
import org.powerbot.game.client.Cache;
import org.powerbot.game.client.Client;
import org.powerbot.game.client.HashTable;
import org.powerbot.game.client.RSItem;
import org.powerbot.game.client.RSItemDef;
import org.powerbot.game.client.RSItemDefLoader;
import org.powerbot.script.internal.Nodes;

public class Item {//TODO complete
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

	public int getId() {
		return this.id;
	}

	public int getStackSize() {
		return this.stackSize;
	}

	public Component getComponent() {
		return component;
	}

	public ItemDefinition getDefinition() {
		final Client client = Bot.client();
		if (client == null) return null;

		final RSItemDefLoader loader;
		final Cache cache;
		final HashTable table;
		if ((loader = client.getRSItemDefLoader()) == null ||
				(cache = loader.getCache()) == null || (table = cache.getTable()) == null) return null;
		final Object o = Nodes.lookup(table, this.id);
		return o != null && o instanceof RSItemDef ? new ItemDefinition((RSItemDef) o) : null;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || !(o instanceof Item)) return false;
		final Item i = (Item) o;
		return this.id == i.id && this.stackSize == i.stackSize &&
				(this.component == null || this.component.equals(i.component));
	}
}
