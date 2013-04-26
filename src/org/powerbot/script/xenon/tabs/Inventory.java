package org.powerbot.script.xenon.tabs;

import java.util.LinkedHashSet;
import java.util.Set;

import org.powerbot.script.xenon.Widgets;
import org.powerbot.script.xenon.wrappers.Component;
import org.powerbot.script.xenon.wrappers.Item;

public class Inventory {
	public static final int WIDGET = 679;
	private static final int[] ALTERNATIVE_WIDGETS = {};

	public static Set<Item> getItems() {
		final Set<Item> items = new LinkedHashSet<>();
		final Component inv = getComponent();
		if (inv == null) return items;
		final Component[] comps = inv.getChildren();
		if (comps.length > 27) for (int i = 0; i < 28; i++) {
			if (comps[i].getItemId() != -1) items.add(new Item(comps[i]));
		}
		return items;
	}

	public static Item getItemAt(final int index) {
		final Component inv = getComponent();
		if (inv == null) return null;
		final Component[] comps = inv.getChildren();
		return index >= 0 && index < 28 && comps.length > 27 && comps[index].getItemId() != -1 ? new Item(comps[index]) : null;
	}

	public static Item getItem(final int... itemIds) {
		return null;
	}

	public static int getSelectedItemIndex() {
		final Component inv = getComponent();
		if (inv == null) return -1;
		final Component[] comps = inv.getChildren();
		if (comps.length > 27) for (int i = 0; i < 28; i++) {
			if (comps[i].getBorderThickness() == 2) return i;
		}
		return -1;
	}

	public static Item getSelectedItem() {
		return getItemAt(getSelectedItemIndex());
	}

	public static boolean isItemSelected() {
		return getSelectedItemIndex() != -1;
	}

	public static int indexOf(final int id) {
		final Component inv = getComponent();
		if (inv == null) return -1;
		final Component[] comps = inv.getChildren();
		if (comps.length > 27) for (int i = 0; i < 28; i++) {
			if (comps[i].getItemId() == id) return i;
		}
		return -1;
	}

	public static boolean contains(final int id) {
		return indexOf(id) != -1;
	}

	public static boolean containsAll(final int... ids) {
		for (final int id : ids) if (indexOf(id) == -1) return false;
		return true;
	}

	public static boolean containsOneOf(final int... ids) {
		for (final int id : ids) if (indexOf(id) != -1) return true;
		return false;
	}

	public static int getCount() {
		return getCount(false);
	}

	public static int getCount(final boolean stacks) {
		int count = 0;
		final Component inv = getComponent();
		if (inv == null) return 0;
		final Component[] comps = inv.getChildren();
		if (comps.length > 27) for (int i = 0; i < 28; i++) {
			if (comps[i].getItemId() != -1) if (stacks) count += comps[i].getItemStackSize();
			else ++count;
		}
		return count;
	}

	public static int getCount(final int... ids) {
		return getCount(false, ids);
	}

	public static int getCount(final boolean stacks, final int... ids) {
		int count = 0;
		final Component inv = getComponent();
		if (inv == null) return 0;
		final Component[] comps = inv.getChildren();
		if (comps.length > 27) for (int i = 0; i < 28; i++) {
			for (final int id : ids) {
				if (comps[i].getItemId() == id) {
					if (stacks) count += comps[i].getItemStackSize();
					else ++count;
					break;
				}
			}
		}
		return count;
	}

	public static boolean isFull() {
		return getCount() == 28;
	}

	private static Component getComponent() {
		Component c;
		for (final int index : ALTERNATIVE_WIDGETS) if ((c = Widgets.get(index, 0)) != null && c.isValid()) return c;
		return Widgets.get(WIDGET, 0);
	}
}
