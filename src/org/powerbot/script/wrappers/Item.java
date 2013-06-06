package org.powerbot.script.wrappers;

import org.powerbot.bot.World;
import org.powerbot.client.Cache;
import org.powerbot.client.Client;
import org.powerbot.client.HashTable;
import org.powerbot.client.RSItemDef;
import org.powerbot.client.RSItemDefLoader;
import org.powerbot.script.methods.Game;
import org.powerbot.util.StringUtil;

public class Item implements Validatable {
	private final int id;
	private int stack;
	private final Component component;

	public Item(Component component) {
		this(component.getItemId(), component.getItemStackSize(), component);
	}

	public Item(int id, int stack, Component component) {
		this.id = id;
		this.stack = stack;
		this.component = component;
	}

	public int getId() {
		return this.id;
	}

	public int getStackSize() {
		int stack = component.getItemStackSize();
		if (component.isVisible() && component.getItemId() == this.id) return this.stack = stack;
		return this.stack;
	}

	public String getName() {
		String name = null;
		if (component != null && component.getItemId() == this.id) name = component.getItemName();
		else {
			final ItemDefinition def;
			if ((def = getDefinition()) != null) name = def.getName();
		}
		return name != null ? StringUtil.stripHtml(name) : null;
	}

	public Component getComponent() {
		return component;
	}

	public ItemDefinition getDefinition() {
		final Client client = World.getWorld().getClient();
		if (client == null) return null;

		final RSItemDefLoader loader;
		final Cache cache;
		final HashTable table;
		if ((loader = client.getRSItemDefLoader()) == null ||
				(cache = loader.getCache()) == null || (table = cache.getTable()) == null) return null;
		final Object o = Game.lookup(table, this.id);
		return o != null && o instanceof RSItemDef ? new ItemDefinition((RSItemDef) o) : null;
	}

	@Override
	public boolean isValid() {
		if (this.component != null && this.component.isValid() && this.component.getItemId() == this.id) return true;
		return false;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || !(o instanceof Item)) return false;
		final Item i = (Item) o;
		return this.id == i.id && this.component.equals(i.component);
	}
}
