package org.powerbot.script.rt4;

import java.util.ArrayList;
import java.util.List;

public class Inventory extends ItemQuery<Item> {
	public Inventory(final ClientContext ctx) {
		super(ctx);
	}

	private static final int WIDGET_BANK = 15;
	private static final int COMPONENT_BANK = 3;

	@Override
	protected List<Item> get() {
		final List<Item> items = new ArrayList<Item>(28);
		final Component sub = getComponent();
		if (sub != null) {
			for (final Component c : sub.components()) {
				final int id = c.itemId();
				if (id <= 0 || id == 6512 || c.itemStackSize() <= 0) {
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

	@Override
	public Item nil() {
		return new Item(ctx, null, -1, -1, -1);
	}

	private Component getComponent() {
		Component component = ctx.widgets.widget(WIDGET_BANK).component(COMPONENT_BANK);
		if (!component.visible()) {
			component = null;
		}
		return component;
	}
}
