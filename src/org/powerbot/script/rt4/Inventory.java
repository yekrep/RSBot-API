package org.powerbot.script.rt4;

import java.util.ArrayList;
import java.util.List;

import org.powerbot.bot.rt4.client.Client;

public class Inventory extends ItemQuery<Item> {
	private static final int WIDGET_BANK = 15;
	private static final int COMPONENT_BANK = 3;

	public Inventory(final ClientContext ctx) {
		super(ctx);
	}

	@Override
	protected List<Item> get() {
		final List<Item> items = new ArrayList<Item>(28);
		final Component sub = getComponent();
		if (sub != null) {
			for (final Component c : sub.components()) {
				final int id = c.itemId();
				if (id <= -1 || id == 6512 || c.itemStackSize() <= 0) {
					continue;
				}
				items.add(new Item(ctx, c, id, c.itemStackSize()));
			}
			return items;
		}

		final Component c = ctx.widgets.widget(149).component(0);
		final int[] ids = c.itemIds(), stacks = c.itemStackSizes();
		for (int i = 0; i < Math.min(ids != null ? ids.length : -1, stacks != null ? stacks.length : -1); i++) {
			final int id = ids[i], stack = stacks[i];
			if (id <= 0) {
				continue;
			}
			items.add(new Item(ctx, c, i, id, stack));
		}
		return items;
	}

	public Item[] items() {
		final Item[] items = new Item[28];
		final Component sub = getComponent();
		if (sub == null) {
			final Component c = ctx.widgets.widget(149).component(0);
			final int[] ids = c.itemIds(), stacks = c.itemStackSizes();
			for (int i = 0; i < Math.min(ids != null ? ids.length : -1, stacks != null ? stacks.length : -1); i++) {
				final int id = ids[i], stack = stacks[i];
				if (id >= 1) {
					items[i] = new Item(ctx, c, i, id, stack);
				} else {
					items[i] = nil();
				}
			}
			return items;
		}

		final Component[] comps = sub.components();
		final int len = comps.length;
		for (int i = 0; i < 28; i++) {
			if (i >= len) {
				items[i] = nil();
				continue;
			}
			final Component c = comps[i];
			final int id = c.itemId();
			if (id <= -1 || id == 6512 || c.itemStackSize() <= 0) {
				items[i] = nil();
				continue;
			}
			items[i] = new Item(ctx, c, id, c.itemStackSize());
		}
		return items;
	}

	public Item itemAt(final int index) {
		return index >= 0 && index < 28 ? items()[index] : nil();
	}

	public int selectionType() {
		final Client client = ctx.client();
		return client != null ? client.getSelectionType() : 0;
	}

	public int selectedItemIndex() {
		final Client client = ctx.client();
		return client != null && selectionType() == 1 ? client.getSelectionIndex() : -1;
	}

	public Item selectedItem() {
		final int index = selectedItemIndex();
		return itemAt(index);
	}

	public Component component() {
		final Component c = getComponent();
		return c != null ? c : ctx.widgets.widget(149).component(0);
	}

	@Override
	public Item nil() {
		return new Item(ctx, null, -1, -1, -1);
	}

	private Component getComponent() {
		Component component = ctx.widgets.widget(Constants.INVENTORY_BANK_WIDGET).component(Constants.INVENTORY_BANK);
		if (!component.visible()) {
			component = null;
		}
		return component;
	}
}
