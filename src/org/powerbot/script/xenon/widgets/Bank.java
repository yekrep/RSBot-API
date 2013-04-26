package org.powerbot.script.xenon.widgets;

import java.util.Arrays;

import org.powerbot.script.xenon.Settings;
import org.powerbot.script.xenon.Widgets;
import org.powerbot.script.xenon.util.Delay;
import org.powerbot.script.xenon.util.Filter;
import org.powerbot.script.xenon.util.Random;
import org.powerbot.script.xenon.util.Timer;
import org.powerbot.script.xenon.wrappers.Component;
import org.powerbot.script.xenon.wrappers.Item;
import org.powerbot.script.xenon.wrappers.Widget;

public class Bank {
	public static final int WIDGET = 762;
	public static final int COMPONENT_CLOSE = 45;
	public static final int COMPONENT_ITEMS = 95;
	public static final int SETTING_BANK_STATE = 1248;

	public static boolean isOpen() {
		final Widget widget = Widgets.get(WIDGET);
		return widget != null && widget.isValid();
	}

	public static boolean close(final boolean wait) {
		if (!isOpen()) return true;
		final Component c = Widgets.get(WIDGET, COMPONENT_CLOSE);
		if (c == null) return false;
		if (c.isValid() && c.interact("Close")) {
			if (!wait) return true;
			final Timer t = new Timer(Random.nextInt(1000, 2000));
			while (t.isRunning() && isOpen()) Delay.sleep(100);
			return !isOpen();
		}
		return false;
	}

	public static boolean close() {
		return close(true);
	}

	public static Item[] getItems() {
		final Component c = Widgets.get(WIDGET, COMPONENT_ITEMS);
		if (c == null || !c.isValid()) return new Item[0];
		final Component[] components = c.getChildren();
		Item[] items = new Item[components.length];
		int d = 0;
		for (final Component i : components) if (i.getItemId() != -1) items[d++] = new Item(i);
		return Arrays.copyOf(items, d);
	}

	public static Item[] getItems(final Filter<Item> filter) {
		final Item[] items = getItems();
		final Item[] arr = new Item[items.length];
		int d = 0;
		for (final Item item : items) if (filter.accept(item)) arr[d++] = item;
		return Arrays.copyOf(arr, d);
	}

	public static Item[] getItems(final boolean currentTab) {
		if (!currentTab) return getItems();
		return getItems(new Filter<Item>() {
			@Override
			public boolean accept(final Item item) {
				return item.getComponent().getRelativeLocation().y != 0;
			}
		});
	}

	public static Item[] getItems(final int... ids) {
		Arrays.sort(ids);
		return getItems(new Filter<Item>() {
			@Override
			public boolean accept(final Item item) {
				return Arrays.binarySearch(ids, item.getId()) >= 0;
			}
		});
	}

	public static Item getItem(final int... ids) {
		final Item[] items = getItems(ids);
		return items.length > 0 ? items[0] : null;
	}

	public static Item getItem(final Filter<Item> filter) {
		final Item[] items = getItems(filter);
		return items.length > 0 ? items[0] : null;
	}

	public static Item[] getItems(final boolean currentTab, final Filter<Item> filter) {
		final Item[] items = getItems(currentTab);
		final Item[] arr = new Item[items.length];
		int d = 0;
		for (final Item item : items) if (filter.accept(item)) arr[d++] = item;
		return Arrays.copyOf(arr, d);
	}

	public static int getCurrentTab() {
		return ((Settings.get(SETTING_BANK_STATE) >>> 24) - 136) / 8;
	}

	public static boolean setCurrentTab(final int index) {
		final Component c = Widgets.get(WIDGET, 63 - (index * 2));
		return c != null && c.isValid() && c.click(true);
	}

	public static Item getTabItem(final int index) {
		final Component c = Widgets.get(WIDGET, 63 - (index * 2));
		if (c != null && c.isValid()) return new Item(c);
		return null;
	}
}
