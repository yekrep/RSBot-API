package org.powerbot.script.rt6;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import org.powerbot.bot.rt6.Items;

/**
 * Utilities pertaining to the in-game backpack.
 */
public class Backpack extends ItemQuery<Item> implements Resizable, Displayable {
	public static final int WIDGET = 1473;
	public static final int COMPONENT_SCROLL_BAR = 30;
	public static final int COMPONENT_VIEW = 31;
	public static final int COMPONENT_CONTAINER = 34;
	public static final int WIDGET_BANK = 762 << 16 | 7;
	public static final int WIDGET_DEPOSIT_BOX = 11 << 16 | 1;
	public static final int WIDGET_GEAR = 1474 << 16 | 13;
	private static final int[] ALTERNATIVE_WIDGETS = {
			WIDGET_BANK,
			WIDGET_DEPOSIT_BOX,
			WIDGET_GEAR,
	};

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
		final int[][] data = ctx.items.getItems(Items.INDEX_INVENTORY);
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
		return component.visible() && component.widget().id() == WIDGET &&
				ctx.widgets.component(WIDGET, COMPONENT_SCROLL_BAR).relativePoint().getX() != 0;
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
		if (backpack.widget().id() == WIDGET) {
			final Rectangle view = ctx.widgets.component(WIDGET, COMPONENT_VIEW).viewportRect();
			final Component c = item.component();
			if (!view.contains(c.boundingRect())) {
				ctx.widgets.scroll(c, ctx.widgets.component(WIDGET, COMPONENT_SCROLL_BAR), view.contains(ctx.input.getLocation()));
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
		final int[][] data = ctx.items.getItems(Items.INDEX_INVENTORY);
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
		final int[][] data = ctx.items.getItems(Items.INDEX_INVENTORY);
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
	 * @return <tt>true</tt> if an item is selected; otherwise <tt>false</tt>
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
		final int[][] data = ctx.items.getItems(Items.INDEX_INVENTORY);
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
		for (final int id : ALTERNATIVE_WIDGETS) {
			if ((c = ctx.widgets.component(id >> 16, id & 0xffff)) != null && c.visible()) {
				return c;
			}
		}
		return ctx.widgets.component(WIDGET, COMPONENT_CONTAINER);
	}

	/**
	 * Returns the amount of money in the money pouch.
	 *
	 * @return the amount of money in the money pouch
	 */
	public int moneyPouchCount() {
		final int[][] arrs = ctx.items.getItems(Items.INDEX_MONEY_POUCH);
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
