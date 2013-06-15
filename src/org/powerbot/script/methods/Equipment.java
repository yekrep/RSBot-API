package org.powerbot.script.methods;

import org.powerbot.script.internal.methods.Items;
import org.powerbot.script.wrappers.Component;
import org.powerbot.script.wrappers.Item;

import java.util.Arrays;

public class Equipment extends ClientLink {
	public static final int WIDGET = 387;
	public static final int WIDGET_BANK = 667;
	public static final int COMPONENT_BANK = 121;
	public static final int NUM_SLOTS = 13;
	public static final int NUM_APPEARANCE_SLOTS = 9;

	public Equipment(ClientFactory factory) {
		super(factory);
	}

	public static enum Slot {
		HEAD(0, 7, 0, 0, -1),
		CAPE(1, 10, 1, 1, -1),
		NECK(2, 13, 2, 2, -1),
		MAIN_HAND(3, 16, 3, 3, 15),
		TORSO(4, 19, 4, 4, -1),
		OFF_HAND(5, 22, 5, 5, 16),
		LEGS(7, 25, 7, 7, -1),
		HANDS(9, 28, 9, 9, -1),
		FEET(10, 31, 10, 10, -1),
		RING(12, 34, 12, -1, -1),
		QUIVER(13, 39, 13, -1, -1),
		AURA(14, 48, 14, 14, -1),
		POCKET(14, 70, 15, -1, -1);
		private final int index;
		private final int component;
		private final int bank;
		private final int appearance;
		private final int sheathed;

		Slot(int index, int component, int bank, int appearance, int sheathed) {
			this.index = index;
			this.component = component;
			this.bank = bank;
			this.appearance = appearance;
			this.sheathed = sheathed;
		}

		public int getIndex() {
			return index;
		}

		public int getComponentIndex() {
			return component;
		}

		public int getBankComponentIndex() {
			return bank;
		}

		public int getAppearanceIndex() {
			return appearance;
		}

		public int getSheathedIndex() {
			return sheathed;
		}
	}

	public Item[] getItems() {
		boolean b = ctx.bank.isOpen();
		int[][] data = ctx.items.getItems(Items.INDEX_EQUIPMENT);
		Item[] items = new Item[NUM_SLOTS];
		for (Slot slot : Slot.values()) {
			int index = slot.getIndex();
			if (index < 0 || index >= data.length || data[index][0] == -1) {
				continue;
			}
			Component c;
			if (b) {
				c = ctx.widgets.get(WIDGET_BANK, COMPONENT_BANK).getChild(slot.getBankComponentIndex());
			} else {
				c = ctx.widgets.get(WIDGET, slot.getComponentIndex());
			}
			items[slot.ordinal()] = new Item(ctx, data[index][0], data[index][1], c);
		}
		return items;
	}

	public Item getItem(Slot slot) {
		int index = slot.getIndex();
		int[][] data = ctx.items.getItems(Items.INDEX_EQUIPMENT);
		if (index < 0 || index >= data.length || data[index][0] == -1) {
			return null;
		}
		Component c;
		if (ctx.bank.isOpen()) {
			c = ctx.widgets.get(WIDGET_BANK, COMPONENT_BANK).getChild(slot.getBankComponentIndex());
		} else {
			c = ctx.widgets.get(WIDGET, slot.getComponentIndex());
		}
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

	public boolean containsOneOf(int... ids) {
		int[][] data = ctx.items.getItems(Items.INDEX_EQUIPMENT);
		for (int id : ids) {
			for (int i = 0; i < data.length; i++) {
				if (data[i][0] == id) {
					return true;
				}
			}
		}
		return false;
	}
}
