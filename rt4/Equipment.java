package org.powerbot.script.rt4;

import java.util.ArrayList;
import java.util.List;

public class Equipment extends ItemQuery<Item> {
	public Equipment(final ClientContext factory) {
		super(factory);
	}

	/**
	 * An enumeration of equipment slots.
	 */
	public enum Slot {
		HEAD(0, 6),
		CAPE(1, 7),
		NECK(2, 8),
		MAIN_HAND(3, 9),
		TORSO(4, 10),
		OFF_HAND(5, 11),
		LEGS(7, 12),
		HANDS(9, 13),
		FEET(10, 14),
		RING(12, 25),
		QUIVER(13, 16);
		private final int index, component;

		Slot(final int index, final int component) {
			this.index = index;
			this.component = component;
		}

		public int getIndex() {
			return index;
		}

		public int getComponentIndex() {
			return component;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<Item> get() {
		final List<Item> items = new ArrayList<Item>(11);
		final Component component = ctx.widgets.widget(Constants.EQUIPMENT_WIDGET).component(Constants.EQUIPMENT_GEAR);
		final int[] ids = component.itemIds(), stacks = component.itemStackSizes();
		for (final Slot slot : Slot.values()) {
			final int index = slot.getIndex();
			if (index >= ids.length || index >= stacks.length) {
				continue;
			}
			final int id = ids[index], stack = stacks[index];
			if (id == -1 || stack <= 0) {
				continue;
			}
			items.add(new Item(ctx, ctx.widgets.widget(Constants.EQUIPMENT_WIDGET).component(slot.getComponentIndex()).component(1), id, stack));
		}
		return items;
	}

	/**
	 * Returns the {@link org.powerbot.script.rt4.Item} at the spcified {@link Slot}.
	 *
	 * @param slot the {@link Slot} to get the {@link org.powerbot.script.rt4.Item} at
	 * @return the {@link org.powerbot.script.rt4.Item} in the provided slot
	 */
	public Item itemAt(final Slot slot) {
		final Component component = ctx.widgets.widget(Constants.EQUIPMENT_WIDGET).component(Constants.EQUIPMENT_GEAR);
		final int[] ids = component.itemIds(), stacks = component.itemStackSizes();
		final int index = slot.getIndex();
		if (index >= ids.length || index >= stacks.length) {
			return nil();
		}
		final int id = ids[index], stack = stacks[index];
		if (id <= 0 || stack <= 0) {
			return nil();
		}
		return new Item(ctx, ctx.widgets.widget(Constants.EQUIPMENT_WIDGET).component(slot.getComponentIndex()), id, stack);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Item nil() {
		return new Item(ctx, null, -1, -1);
	}
}
