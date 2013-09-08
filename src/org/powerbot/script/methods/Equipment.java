package org.powerbot.script.methods;

import java.util.ArrayList;
import java.util.List;

import org.powerbot.script.internal.methods.Items;
import org.powerbot.script.lang.ItemQuery;
import org.powerbot.script.wrappers.Component;
import org.powerbot.script.wrappers.Item;

import static org.powerbot.script.util.Constants.getInt;

public class Equipment extends ItemQuery<Item> {
	public static final int WIDGET = getInt("equipment.widget");
	public static final int COMPONENT_CONTAINER = getInt("equipment.component.container");
	public static final int WIDGET_GEAR = getInt("equipment.widget.gear");
	public static final int COMPONENT_GEAR_CONTAINER = getInt("equipment.component.gear.container");
	public static final int NUM_SLOTS = getInt("equipment.num.slots");

	public Equipment(MethodContext factory) {
		super(factory);
	}

	/**
	 * An enumeration of equipment slots.
	 *
	 * @author Timer
	 */
	public static enum Slot {
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
		POCKET(14, 15);
		private final int storageIndex;
		private final int component;

		Slot(int storageIndex, int component) {
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<Item> get() {
		List<Item> items = new ArrayList<>(28);
		int[][] data = ctx.items.getItems(Items.INDEX_EQUIPMENT);
		Component component = getComponent();
		for (Slot slot : Slot.values()) {
			int index = slot.getStorageIndex();
			Component c = component.getChild(slot.getComponentIndex());
			if (index < 0 || index >= data.length || data[index][0] == -1) {
				continue;
			}
			items.add(new Item(ctx, data[index][0], data[index][1], c));
		}
		return items;
	}

	/**
	 * Returns the {@link Item} at the spcified {@link Slot}.
	 *
	 * @param slot the {@link Slot} to get the {@link Item} at
	 * @return the {@link Item} in the provided slot
	 */
	public Item getItemAt(Slot slot) {
		int index = slot.getStorageIndex();
		int[][] data = ctx.items.getItems(Items.INDEX_EQUIPMENT);
		Component c = getComponent().getChild(slot.getComponentIndex());
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
	public Component getComponent() {
		Component gear = ctx.widgets.get(WIDGET_GEAR, COMPONENT_GEAR_CONTAINER);
		return gear.isVisible() ? gear : ctx.widgets.get(WIDGET, COMPONENT_CONTAINER);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Item getNil() {
		return new Item(ctx, -1, -1, null);
	}
}
