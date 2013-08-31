package org.powerbot.script.methods;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import org.powerbot.script.internal.methods.Items;
import org.powerbot.script.lang.ItemQuery;
import org.powerbot.script.wrappers.Component;
import org.powerbot.script.wrappers.Displayable;
import org.powerbot.script.wrappers.Item;

public class Backpack extends ItemQuery<Item> implements Resizable {
	public static final int WIDGET = 1473;
	public static final int COMPONENT_SCROLL_BAR = 6;
	public static final int COMPONENT_VIEW = 7;
	public static final int COMPONENT_CONTAINER = 8;
	public static final int WIDGET_BANK = 762 << 16 | 54;
	public static final int WIDGET_DEPOSIT_BOX = 11 << 16 | 15;
	public static final int WIDGET_GEAR = 1474 << 16 | 13;
	private static final int[] ALTERNATIVE_WIDGETS = {
			WIDGET_BANK,
			WIDGET_DEPOSIT_BOX,
			WIDGET_GEAR,
	};

	public Backpack(MethodContext factory) {
		super(factory);
	}

	@Override
	protected List<Item> get() {
		List<Item> items = new ArrayList<>(28);
		Component inv = getComponent();
		int[][] data = ctx.items.getItems(Items.INDEX_INVENTORY);
		for (int i = 0; i < 28; i++) {
			Component comp = inv.getChild(i);
			if (i >= data.length) {
				break;
			}
			if (data[i][0] == -1) {
				continue;
			}
			items.add(new Item(ctx, data[i][0], data[i][1], comp));
		}
		return items;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isCollapsed() {
		Component component = getComponent();
		if (!component.isVisible()) {
			return false;
		}
		if (component.getWidget().getIndex() == WIDGET) {
			return ctx.widgets.get(WIDGET, COMPONENT_SCROLL_BAR).getRelativeLocation().getX() != 0;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean scroll(Displayable item) {
		if (!isCollapsed()) {
			return true;
		}
		Component backpack = getComponent();
		if (backpack.getWidget().getIndex() == WIDGET) {
			Rectangle view = ctx.widgets.get(WIDGET, COMPONENT_VIEW).getViewportRect();
			Component c = item.getComponent();
			if (!view.contains(c.getBoundingRect())) {
				ctx.widgets.scroll(c, ctx.widgets.get(WIDGET, COMPONENT_SCROLL_BAR), view.contains(ctx.mouse.getLocation()));
			}
			return view.contains(c.getBoundingRect());
		}
		return false;
	}

	public Item[] getAllItems() {
		Item[] items = new Item[28];
		Component inv = getComponent();
		int[][] data = ctx.items.getItems(Items.INDEX_INVENTORY);
		for (int i = 0; i < 28; i++) {
			Component comp = inv.getChild(i);
			if (i < data.length) {
				items[i] = new Item(ctx, data[i][0], data[i][1], comp);
			} else {
				items[i] = getNil();
			}
		}
		return items;
	}

	public Item getItemAt(final int index) {
		Component inv = getComponent();
		int[][] data = ctx.items.getItems(Items.INDEX_INVENTORY);
		if (index >= 0 && index < 28 && index < data.length && data[index][0] != -1) {
			return new Item(ctx, data[index][0], data[index][1], inv.getChild(index));
		}
		return getNil();
	}

	public int getSelectedItemIndex() {
		Component inv = getComponent();
		for (int i = 0; i < 28; i++) {
			if (inv.getChild(i).getBorderThickness() == 2) {
				return i;
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

	public Component getComponent() {
		Component c;
		for (final int id : ALTERNATIVE_WIDGETS) {
			if ((c = ctx.widgets.get(id >> 16, id & 0xffff)) != null && c.isVisible()) {
				return c;
			}
		}
		return ctx.widgets.get(WIDGET, COMPONENT_CONTAINER);
	}

	public int getMoneyPouch() {
		int[][] arrs = ctx.items.getItems(Items.INDEX_MONEY_POUCH);
		for (int[] arr : arrs) {
			if (arr[0] == 995) {
				return arr[1];
			}
		}
		return 0;
	}

	@Override
	public Item getNil() {
		return new Item(ctx, -1, -1, null);
	}
}
