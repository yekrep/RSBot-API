package org.powerbot.script.rt6;

import java.util.ArrayList;
import java.util.List;

/**
 * Equipment
 */
public class Equipment extends ItemQuery<Item> implements Displayable {
	public Equipment(final ClientContext factory) {
		super(factory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<Item> get() {
		final List<Item> items = new ArrayList<Item>(28);
		final int[][] data = ctx.items.getItems(Constants.ITEMS_EQUIPMENT);
		final Component component = component();
		for (final Slot slot : Slot.values()) {
			final int index = slot.getStorageIndex();
			final Component c = component.component(slot.getComponentIndex());
			if (index < 0 || index >= data.length || data[index][0] == -1) {
				continue;
			}
			items.add(new Item(ctx, data[index][0], data[index][1], c));
		}
		return items;
	}

	/**
	 * Returns the {@link Item} at the specified {@link Slot}.
	 *
	 * @param slot the {@link Slot} to get the {@link Item} at
	 * @return the {@link Item} in the provided slot
	 */
	public Item itemAt(final Slot slot) {
		final int index = slot.getStorageIndex();
		final int[][] data = ctx.items.getItems(Constants.ITEMS_EQUIPMENT);
		final Component c = component().component(slot.getComponentIndex());
		if (index >= data.length || data[index][0] == -1) {
			return new Item(ctx, -1, -1, c);
		}
		return new Item(ctx, data[index][0], data[index][1], c);
	}

	/**
	 * Returns the {@link Component} of the equipment display
	 *
	 * @return the {@link Component} of the equipment display
	 */
	public Component component() {
		final Component gear = ctx.widgets.component(Constants.EQUIPMENT_GEAR, Constants.EQUIPMENT_GEAR_CONTAINER);
		return gear.visible() ? gear : ctx.widgets.component(Constants.EQUIPMENT_WIDGET, Constants.EQUIPMENT_CONTAINER);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Item nil() {
		return new Item(ctx, -1, -1, null);
	}

	/**
	 * An enumeration of equipment slots.
	 */
	public enum Slot {
		HEAD(0, 0),
		CAPE(1, 1),
		NECK(2, 2),
		MAIN_HAND(3, 3),
		TORSO(4, 4),
		OFF_HAND(5, 5),
		LEGS(7, 7),
		HANDS(9, 9),
		FEET(10, 10),
		RING(12, 12),
		QUIVER(13, 13),
		AURA(14, 14),
		POCKET(15, 15);
		private final int storageIndex;
		private final int component;

		Slot(final int storageIndex, final int component) {
			this.storageIndex = storageIndex;
			this.component = component;
		}

		public int getStorageIndex() {
			return storageIndex;
		}

		public int getComponentIndex() {
			return component;
		}
	}
}
