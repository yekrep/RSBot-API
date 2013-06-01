package org.powerbot.script.methods.tabs;

import java.util.Arrays;

import org.powerbot.script.methods.Game;
import org.powerbot.script.methods.World;
import org.powerbot.script.methods.WorldImpl;
import org.powerbot.script.util.Filter;
import org.powerbot.script.wrappers.Component;
import org.powerbot.script.wrappers.Item;

public class Inventory extends WorldImpl {
	public static final int WIDGET = 679;
	public static final int WIDGET_BANK = 763;
	public static final int WIDGET_PRICE_CHECK = 204;
	public static final int WIDGET_EQUIPMENT_BONUSES = 670;
	public static final int WIDGET_EXCHANGE = 644;
	public static final int WIDGET_SHOP = 621;
	public static final int WIDGET_DUNGEONEERING_SHOP = 957;
	public static final int WIDGET_BEAST_OF_BURDEN_STORAGE = 665;
	public static final int WIDGET_STORE = 1266;
	public static final int WIDGET_SAWMILL_CART = 771;
	private static final int[] ALTERNATIVE_WIDGETS = {
			WIDGET_BANK,
			WIDGET_PRICE_CHECK, WIDGET_EQUIPMENT_BONUSES,
			WIDGET_EXCHANGE, WIDGET_SHOP, WIDGET_DUNGEONEERING_SHOP,
			WIDGET_BEAST_OF_BURDEN_STORAGE, WIDGET_STORE, WIDGET_SAWMILL_CART
	};

	public Inventory(World world) {
		super(world);
	}

	public Item[] getItems() {
		final Item[] items = new Item[28];
		final Component inv = getComponent();
		if (inv == null) return items;
		int d = 0;
		final Component[] comps = inv.getChildren();
		if (comps.length > 27) for (int i = 0; i < 28; i++) {
			if (comps[i].getItemId() != -1) items[d++] = new Item(world, comps[i]);
		}
		return Arrays.copyOf(items, d);
	}

	public Item[] getAllItems() {
		final Item[] items = new Item[28];
		final Component inv = getComponent();
		if (inv == null) return items;
		final Component[] comps = inv.getChildren();
		if (comps.length > 27) for (int i = 0; i < 28; i++) {
			items[i] = new Item(world, comps[i]);
		}
		return items;
	}

	public Item getItemAt(final int index) {
		final Component inv = getComponent();
		if (inv == null) return null;
		final Component[] comps = inv.getChildren();
		return index >= 0 && index < 28 && comps.length > 27 && comps[index].getItemId() != -1 ? new Item(world, comps[index]) : null;
	}

	public Item[] getItems(final Filter<Item> filter) {
		final Item[] items = getItems();
		final Item[] set = new Item[items.length];
		int d = 0;
		for (final Item item : items) if (filter.accept(item)) set[d++] = item;
		return Arrays.copyOf(set, d);
	}

	public Item[] getItems(final int... ids) {
		return getItems(new Filter<Item>() {
			@Override
			public boolean accept(final Item item) {
				final int _id = item.getId();
				for (final int id : ids) if (id == _id) return true;
				return false;
			}
		});
	}

	public Item getItem(final Filter<Item> filter) {
		final Item[] items = getItems(filter);
		return items != null && items.length > 0 ? items[0] : null;
	}

	public Item getItem(final int... ids) {
		return getItem(new Filter<Item>() {
			@Override
			public boolean accept(final Item item) {
				final int _id = item.getId();
				for (final int id : ids) if (id == _id) return true;
				return false;
			}
		});
	}

	public int getSelectedItemIndex() {
		final Component inv = getComponent();
		if (inv == null) return -1;
		final Component[] comps = inv.getChildren();
		if (comps.length > 27) for (int i = 0; i < 28; i++) {
			if (comps[i].getBorderThickness() == 2) return i;
		}
		return -1;
	}

	public Item getSelectedItem() {
		return getItemAt(getSelectedItemIndex());
	}

	public boolean isItemSelected() {
		return getSelectedItemIndex() != -1;
	}

	public int indexOf(final int id) {
		final Component inv = getComponent();
		if (inv == null) return -1;
		final Component[] comps = inv.getChildren();
		if (comps.length > 27) for (int i = 0; i < 28; i++) if (comps[i].getItemId() == id) return i;
		return -1;
	}

	public boolean contains(final int id) {
		return indexOf(id) != -1;
	}

	public boolean containsAll(final int... ids) {
		for (final int id : ids) if (indexOf(id) == -1) return false;
		return true;
	}

	public boolean containsOneOf(final int... ids) {
		for (final int id : ids) if (indexOf(id) != -1) return true;
		return false;
	}

	public int getCount() {
		return getCount(false);
	}

	public int getCount(final boolean stacks) {
		int count = 0;
		final Component inv = getComponent();
		if (inv == null) return 0;
		final Component[] comps = inv.getChildren();
		if (comps.length > 27) for (int i = 0; i < 28; i++) {
			if (comps[i].getItemId() != -1) if (stacks) count += comps[i].getItemStackSize();
			else ++count;
		}
		return count;
	}

	public int getCount(final int... ids) {
		return getCount(false, ids);
	}

	public int getCount(final boolean stacks, final int... ids) {
		int count = 0;
		final Component inv = getComponent();
		if (inv == null) return 0;
		final Component[] comps = inv.getChildren();
		if (comps.length > 27) for (int i = 0; i < 28; i++) {
			for (final int id : ids) {
				if (comps[i].getItemId() == id) {
					if (stacks) count += comps[i].getItemStackSize();
					else ++count;
					break;
				}
			}
		}
		return count;
	}

	public boolean isFull() {
		return getCount() == 28;
	}

	public boolean isEmpty() {
		return getCount() == 0;
	}

	private Component getComponent() {
		Component c;
		for (final int index : ALTERNATIVE_WIDGETS)
			if ((c = world.widgets.get(index, 0)) != null && c.isValid()) return c;
		world.game.openTab(Game.TAB_INVENTORY);
		return world.widgets.get(WIDGET, 0);
	}
}
