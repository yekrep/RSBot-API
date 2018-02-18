package org.powerbot.script.rt6;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * Backpack
 * Utilities pertaining to the in-game backpack.
 */
public class Backpack extends ItemQuery<Item> implements Resizable, Displayable {
	public Backpack(final ClientContext factory) {
		super(factory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<Item> get() {
		final List<Item> items = new ArrayList<Item>(28);
		final Component inv = component();
		final int[][] data = ctx.items.getItems(Constants.ITEMS_INVENTORY);
		for (int i = 0; i < 28; i++) {
			final Component comp = inv.component(i);
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
	public boolean collapsed() {
		final Component component = component();
		return component.visible() && component.widget().id() == Constants.BACKPACK_WIDGET &&
				ctx.widgets.component(Constants.BACKPACK_WIDGET, Constants.BACKPACK_SCROLLBAR).relativePoint().getX() != 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean scroll(final Displayable item) {
		if (!collapsed()) {
			return true;
		}
		final Component backpack = component();
		if (backpack.widget().id() == Constants.BACKPACK_WIDGET) {
			final Rectangle view = ctx.widgets.component(Constants.BACKPACK_WIDGET, Constants.BACKPACK_VIEW).viewportRect();
			final Component c = item.component();
			if (!view.contains(c.boundingRect())) {
				ctx.widgets.scroll(c, ctx.widgets.component(Constants.BACKPACK_WIDGET, Constants.BACKPACK_SCROLLBAR), view.contains(ctx.input.getLocation()));
			}
			return view.contains(c.boundingRect());
		}
		return false;
	}

	/**
	 * Returns an array of all the items in the inventory.
	 *
	 * @return all the items in the inventory
	 */
	public Item[] items() {
		final Item[] items = new Item[28];
		final Component inv = component();
		final int[][] data = ctx.items.getItems(Constants.ITEMS_INVENTORY);
		for (int i = 0; i < 28; i++) {
			final Component comp = inv.component(i);
			if (i < data.length) {
				items[i] = new Item(ctx, data[i][0], data[i][1], comp);
			} else {
				items[i] = nil();
			}
		}
		return items;
	}

	/**
	 * Returns the item at a specific index.
	 *
	 * @param index the index of the item
	 * @return the {@link Item} in the slot given
	 */
	public Item itemAt(final int index) {
		final Component inv = component();
		final int[][] data = ctx.items.getItems(Constants.ITEMS_INVENTORY);
		if (index >= 0 && index < 28 && index < data.length && data[index][0] != -1) {
			return new Item(ctx, data[index][0], data[index][1], inv.component(index));
		}
		return nil();
	}

	/**
	 * Returns the index of the currently selected item.
	 *
	 * @return the index of the selected item
	 */
	public int selectedItemIndex() {
		final Component inv = component();
		for (int i = 0; i < 28; i++) {
			if (inv.component(i).borderThickness() == 2) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Returns if an item is selected or not.
	 *
	 * @return {@code true} if an item is selected; otherwise {@code false}
	 */
	public boolean itemSelected() {
		return selectedItemIndex() != -1;
	}

	/**
	 * Returns the index of the item with the given id, or the first index of it.
	 *
	 * @param id the id of the item
	 * @return the index
	 */
	public int indexOf(final int id) {
		final int[][] data = ctx.items.getItems(Constants.ITEMS_INVENTORY);
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

	/**
	 * Returns the {@link Component} of the inventory
	 *
	 * @return the inventory {@link Component}
	 */
	@Override
	public Component component() {
		Component c;
		for (final int id : Constants.BACKPACK_ALTERNATIVES) {
			if ((c = ctx.widgets.component(id >> 16, id & 0xffff)) != null && c.visible()) {
				return c;
			}
		}
		return ctx.widgets.component(Constants.BACKPACK_WIDGET, Constants.BACKPACK_CONTAINER);
	}

	/**
	 * Returns the amount of money in the money pouch.
	 *
	 * @return the amount of money in the money pouch
	 */
	public int moneyPouchCount() {
		final int[][] arrs = ctx.items.getItems(Constants.ITEMS_POUCH);
		for (final int[] arr : arrs) {
			if (arr[0] == 995) {
				return arr[1];
			}
		}
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Item nil() {
		return new Item(ctx, -1, -1, null);
	}
}
