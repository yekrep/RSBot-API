package org.powerbot.script.xenon.wrappers;

import org.powerbot.bot.Bot;
import org.powerbot.client.Cache;
import org.powerbot.client.Client;
import org.powerbot.client.HashTable;
import org.powerbot.client.RSItem;
import org.powerbot.client.RSItemDef;
import org.powerbot.client.RSItemDefLoader;
import org.powerbot.script.internal.Nodes;

public class Item implements Validatable {
	private final int id, stackSize;
	private final Component component;
	private final RSItem item;

	public Item(final RSItem item) {
		this.id = item.getId();
		this.stackSize = item.getStackSize();
		this.component = null;
		this.item = item;
	}

	public Item(final Component component) {
		this.id = component.getItemId();
		this.stackSize = component.getItemStackSize();
		this.component = component;
		this.item = null;
	}

	public int getId() {
		return this.id;
	}

	public int getStackSize() {
		return this.stackSize;
	}

	public String getName() {
		String name = null;
		if (component != null) name = component.getItemName();
		else {
			final ItemDefinition def;
			if ((def = getDefinition()) != null) name = def.getName();
		}
		return name != null ? name.replaceAll("\\<.*?>", "") : null;
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
		return o != null && o instanceof RSItemDef ? new ItemDefinition(this, (RSItemDef) o) : null;
	}

	@Override
	public boolean isValid() {
		return false;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || !(o instanceof Item)) return false;
		final Item i = (Item) o;
		return this.id == i.id && this.stackSize == i.stackSize &&
				(this.component == null || this.component.equals(i.component)) &&
				(this.item == null || this.item == i.item);
	}
}
