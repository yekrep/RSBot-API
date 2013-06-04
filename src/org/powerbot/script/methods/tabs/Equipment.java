package org.powerbot.script.methods.tabs;

import org.powerbot.script.methods.Players;
import org.powerbot.script.methods.Widgets;
import org.powerbot.script.methods.widgets.Bank;
import org.powerbot.script.wrappers.Component;
import org.powerbot.script.wrappers.Item;
import org.powerbot.script.wrappers.Player;
import org.powerbot.script.wrappers.Widget;

public class Equipment {
	public static final int WIDGET = 387;
	public static final int WIDGET_BANK = 667;
	public static final int COMPONENT_BANK = 7;
	public static final int NUM_SLOTS = 13;
	public static final int NUM_APPEARANCE_SLOTS = 9;

	public static enum Slot {
		HEAD(7, 0, 0, -1),
		CAPE(10, 1, 1, -1),
		NECK(13, 2, 2, -1),
		MAIN_HAND(16, 3, 3, 15),
		TORSO(19, 4, 4, -1),
		OFF_HAND(22, 5, 5, 16),
		LEGS(25, 7, 7, -1),
		HANDS(28, 9, 9, -1),
		FEET(31, 10, 10, -1),
		RING(34, 12, -1, -1),
		QUIVER(39, 13, -1, -1),
		AURA(48, 14, 14, -1),
		POCKET(70, 15, -1, -1);
		private final int component;
		private final int bank;
		private final int appearance;
		private final int sheathed;

		Slot(final int component, final int bank, final int appearance, final int sheathed) {
			this.component = component;
			this.bank = bank;
			this.appearance = appearance;
			this.sheathed = sheathed;
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

	public static boolean appearanceContainsAll(final int... itemIds) {
		final int[] visibleEquipment = getAppearanceIds();
		for (final int id : itemIds) {
			boolean hasItem = false;
			for (final int i : visibleEquipment) {
				if (i == id) {
					hasItem = true;
					break;
				}
			}
			if (!hasItem) return false;
		}
		return true;
	}

	public static boolean appearanceContainsOneOf(final int... itemIds) {
		for (final int id : getAppearanceIds()) {
			for (final int i : itemIds) if (i == id) return true;
		}
		return false;
	}

	public static boolean containsAll(final int... ids) {
		final Item[] items = getItems();
		for (final Item item : items) {
			if (item == null) continue;
			boolean hasItem = false;
			final int _id = item.getId();
			for (final int id : ids) {
				if (_id == id) {
					hasItem = true;
					break;
				}
			}
			if (!hasItem) return false;
		}
		return true;
	}

	public static boolean containsOneOf(final int... ids) {
		for (final Item item : getItems()) {
			final int _id = item.getId();
			for (final int id : ids) if (_id == id) return true;
		}
		return false;
	}

	public static int getAppearanceId(final Slot slot) {
		final Player p = Players.getLocal();
		final int[] app;
		if (slot.getAppearanceIndex() == -1 || p == null || (app = p.getAppearance()) == null) {
			return -1;
		}
		int id = app[slot.getAppearanceIndex()];
		if (id <= 0 && slot.getSheathedIndex() != -1) {
			id = app[slot.getSheathedIndex()];
		}
		return id > 0 ? id : -1;
	}

	public static int[] getAppearanceIds() {
		final int[] ids = new int[NUM_APPEARANCE_SLOTS];
		for (int i = 0; i < ids.length; i++) ids[i] = -1;
		final Player p = Players.getLocal();
		final int[] app;
		if (p == null || (app = p.getAppearance()) == null) {
			return ids;
		}
		final Slot[] slots = Slot.values();
		for (int i = 0; i < ids.length; i++) {
			final int index = app[slots[i].getAppearanceIndex()] > 0 ?
					slots[i].getAppearanceIndex() : slots[i].getSheathedIndex();
			if (index != -1) {
				final int id = app[index];
				ids[i] = id > 0 ? id : -1;
			}
		}
		return ids;
	}

	public static Item[] getCachedItems() {
		final Widget widget = Widgets.get(WIDGET);
		if (widget != null) {
			final Component[] components = widget.getComponents();
			if (components.length > 0) {
				final Item[] items = new Item[NUM_SLOTS];
				final Slot[] slots = Slot.values();
				for (int i = 0; i < NUM_SLOTS; i++) {
					items[i] = new Item(components[slots[i].getComponentIndex()]);
				}
				return items;
			}
		}
		return new Item[0];
	}

	public static int getCount() {
		return NUM_SLOTS - getCount(-1);
	}

	public static int getCount(final int... itemIds) {
		int count = 0;
		for (final Item item : getItems()) {
			if (item == null) continue;
			final int itemId = item.getId();
			for (final int id : itemIds) {
				if (itemId == id) {
					count++;
					break;
				}
			}
		}
		return count;
	}

	public static Item getItem(final Slot slot) {
		final Widget widget = getWidget();
		if (widget != null && widget.isValid()) {
			final Component itemComp = widget.getIndex() == WIDGET_BANK ?
					widget.getComponent(COMPONENT_BANK).getChild(slot.getBankComponentIndex()) :
					widget.getComponent(slot.getComponentIndex());
			if (itemComp != null) return new Item(itemComp);
		}
		return null;
	}

	public static Item getItem(final int... itemIds) {
		for (final Item item : getItems()) {
			if (item == null) continue;
			for (final int itemId : itemIds) {
				if (itemId == item.getId()) {
					return item;
				}
			}
		}
		return null;
	}

	public static Item getCachedItem(final int... itemIds) {
		for (final Item item : getCachedItems()) {
			if (item == null) continue;
			for (final int itemId : itemIds) {
				if (itemId == item.getId()) {
					return item;
				}
			}
		}
		return null;
	}

	public static Item getCachedItem(final Slot slot) {
		final Widget cache = Widgets.get(WIDGET);
		if (cache != null && cache.isValid()) {
			return new Item(cache.getComponent(slot.getComponentIndex()));
		}
		return null;
	}

	public static Item[] getItems() {
		final Widget widget = getWidget();
		if (widget != null) {
			final boolean b = widget.getIndex() != WIDGET;
			final Component[] equip = b ? widget.getComponent(COMPONENT_BANK).getChildren() : widget.getComponents();
			if (equip.length > 0) {
				if (!b) {
					final Item[] items = new Item[NUM_SLOTS];
					final Slot[] slots = Slot.values();
					for (int i = 0; i < NUM_SLOTS; i++) {
						items[i] = new Item(equip[slots[i].getComponentIndex()]);
					}
					return items;
				} else {
					final Item[] items = new Item[equip.length];
					for (int i = 0; i < items.length; i++) {
						items[i] = new Item(equip[i]);
					}
					return items;
				}
			}
		}
		return new Item[0];
	}

	private static Widget getWidget() {
		if (Bank.isOpen()) return Widgets.get(WIDGET_BANK);
		return Widgets.get(WIDGET);
	}
}
