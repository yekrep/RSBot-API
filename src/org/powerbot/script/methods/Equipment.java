package org.powerbot.script.methods;

import org.powerbot.script.internal.methods.Items;
import org.powerbot.script.wrappers.Component;
import org.powerbot.script.wrappers.Item;

public class Equipment extends MethodProvider {
	public static final int WIDGET = 1464;
	public static final int COMPONENT_CONTAINER = 28;
	public static final int WIDGET_GEAR = 1462;
	public static final int COMPONENT_GEAR_CONTAINER = 13;
	public static final int NUM_SLOTS = 13;

	public Equipment(MethodContext factory) {
		super(factory);
	}

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

	public Item[] getAllItems() {
		boolean b = ctx.bank.isOpen();
		int[][] data = ctx.items.getItems(Items.INDEX_EQUIPMENT);
		Item[] items = new Item[NUM_SLOTS];
		Component component = getComponent();
		for (Slot slot : Slot.values()) {
			int index = slot.getStorageIndex();
			Component c = component.getChild(slot.getComponentIndex());
			if (index < 0 || index >= data.length || data[index][0] == -1) {
				items[slot.ordinal()] = new Item(ctx, -1, -1, c);
				continue;
			}
			items[slot.ordinal()] = new Item(ctx, data[index][0], data[index][1], c);
		}
		return items;
	}

	public Item getItemAt(Slot slot) {
		int index = slot.getStorageIndex();
		int[][] data = ctx.items.getItems(Items.INDEX_EQUIPMENT);
		if (index < 0 || index >= data.length || data[index][0] == -1) {
			return null;
		}
		Component c = getComponent().getChild(slot.getComponentIndex());
		return new Item(ctx, data[index][0], data[index][1], c);
	}

	public boolean contains(int... ids) {
		int[][] data = ctx.items.getItems(Items.INDEX_EQUIPMENT);
		for (int id : ids) {
			boolean contains = false;
			for (int i = 0; i < data.length; i++) {
				if (data[i][0] == id) {
					contains = true;
					break;
				}
			}
			if (!contains) {
				return false;
			}
		}
		return true;
	}

	public Component getComponent() {
		if (ctx.bank.isOpen()) {
			return ctx.widgets.get(WIDGET_GEAR, COMPONENT_GEAR_CONTAINER);
		}
		return ctx.widgets.get(WIDGET, COMPONENT_CONTAINER);
	}
}
