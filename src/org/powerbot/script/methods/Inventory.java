package org.powerbot.script.methods;

import org.powerbot.script.internal.methods.Items;
import org.powerbot.script.lang.ItemQuery;
import org.powerbot.script.wrappers.Component;
import org.powerbot.script.wrappers.Item;

import java.util.ArrayList;
import java.util.List;

public class Inventory extends ItemQuery<Item> {
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

	public Inventory(MethodContext factory) {
		super(factory);
	}

	@Override
	protected List<Item> get() {
		List<Item> items = new ArrayList<>(28);
		final Component inv = getComponent();
		if (inv == null) {
			return items;
		}
		final Component[] comps = inv.getChildren();
		int[][] data;
		if (inv.isVisible()) {
			data = ctx.items.getItems(Items.INDEX_INVENTORY);
		} else {
			data = null;
		}
		if (comps.length > 27) {
			for (int i = 0; i < 28; i++) {
				if (data != null) {
					if (i >= data.length) {
						break;
					}
					if (data[i][0] == -1) {
						continue;
					}
					items.add(new Item(ctx, data[i][0], data[i][1], comps[i]));
				} else if (comps[i].getItemId() != -1) {
					items.add(new Item(ctx, comps[i]));
				}
			}
		}
		return items;
	}

	public Item[] getAllItems() {
		final Item[] items = new Item[28];
		final Component inv = getComponent();
		if (inv == null) {
			return items;
		}
		final Component[] comps = inv.getChildren();
		int[][] data;
		if (inv.isVisible()) {
			data = ctx.items.getItems(Items.INDEX_INVENTORY);
		} else {
			data = null;
		}
		if (comps.length > 27) {
			for (int i = 0; i < 28; i++) {
				if (data != null) {
					if (i < data.length) {
						items[i] = new Item(ctx, data[i][0], data[i][1], comps[i]);
					} else {
						items[i] = new Item(ctx, -1, -1, comps[i]);
					}
				} else {
					items[i] = new Item(ctx, comps[i]);
				}
			}
		}
		return items;
	}

	public Item getItemAt(final int index) {
		final Component inv = getComponent();
		if (inv == null) {
			return null;
		}
		final Component[] comps = inv.getChildren();
		int[][] data = ctx.items.getItems(Items.INDEX_INVENTORY);
		if (index >= 0 && index < 28 && comps.length > 27 && index < data.length && data[index][0] != -1) {
			return new Item(ctx, data[index][0], data[index][1], comps[index]);
		}
		return null;
	}

	public int getSelectedItemIndex() {
		final Component inv = getComponent();
		if (inv == null) {
			return -1;
		}
		final Component[] comps = inv.getChildren();
		if (comps.length > 27) {
			for (int i = 0; i < 28; i++) {
				if (comps[i].getBorderThickness() == 2) {
					return i;
				}
			}
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
		int[][] data = ctx.items.getItems(Items.INDEX_INVENTORY);
		for (int i = 0; i < 28; i++) {
			if (i < data.length) {
				if (data[i][0] == id) {
					return i;
				}
			} else {
				break;
			}
		}
		return -1;
	}

	private Component getComponent() {
		Component c;
		for (final int index : ALTERNATIVE_WIDGETS) {
			if ((c = ctx.widgets.get(index, 0)) != null && c.isValid()) {
				return c;
			}
		}
		return ctx.widgets.get(WIDGET, 0);
	}

	@Override
	public Item getNil() {
		return new Item(ctx, -1, -1, null);
	}
}
