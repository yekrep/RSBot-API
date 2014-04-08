package org.powerbot.script.rt4;

import java.util.ArrayList;
import java.util.List;

public class Equipment extends ItemQuery<Item> {
	public static final int WIDGET = 387;
	public static final int COMPONENT_GEAR_CONTAINER = 28;

	public Equipment(final ClientContext factory) {
		super(factory);
	}

	/**
	 * An enumeration of equipment slots.
	 */
	public static enum Slot {
		HEAD(0, 12),
		CAPE(1, 13),
		NECK(2, 14),
		MAIN_HAND(3, 16),
		TORSO(4, 17),
		OFF_HAND(5, 18),
		LEGS(7, 19),
		HANDS(9, 21),
		FEET(10, 20),
		RING(12, 22),
		QUIVER(13, 15);
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
		final Component component = ctx.widgets.widget(WIDGET).component(COMPONENT_GEAR_CONTAINER);
		final int[] ids = component.itemIds(), stacks = component.itemStackSizes();
		for (final Slot slot : Slot.values()) {
			final int index = slot.getIndex();
			if (index >= ids.length || index >= stacks.length) {
				continue;
			}
			final int id = ids[index], stack = stacks[index];
			if (id <= 0 || stack <= 0) {
				continue;
			}
			items.add(new Item(ctx, ctx.widgets.widget(WIDGET).component(slot.getComponentIndex()), id, stack));
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
		final Component component = ctx.widgets.widget(WIDGET).component(COMPONENT_GEAR_CONTAINER);
		final int[] ids = component.itemIds(), stacks = component.itemStackSizes();
		final int index = slot.getIndex();
		if (index >= ids.length || index >= stacks.length) {
			return nil();
		}
		final int id = ids[index], stack = stacks[index];
		if (id <= 0 || stack <= 0) {
			return nil();
		}
		return new Item(ctx, ctx.widgets.widget(WIDGET).component(slot.getComponentIndex()), id, stack);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Item nil() {
		return new Item(ctx, null, -1, -1);
	}
}
