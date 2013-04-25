package org.powerbot.script.xenon.widgets;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

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

	public static Set<Item> getItems() {
		final Set<Item> items = new LinkedHashSet<>();
		final Component c = Widgets.get(WIDGET, COMPONENT_ITEMS);
		if (c == null || !c.isValid()) return items;
		final Component[] components = c.getChildren();
		for (final Component i : components) if (i.getItemId() != -1) items.add(new Item(i));
		return items;
	}

	public static Set<Item> getItems(final Filter<Item> filter) {
		final Set<Item> items = getItems();
		final Set<Item> set = new LinkedHashSet<>(items.size());
		for (final Item item : items) if (filter.accept(item)) set.add(item);
		return set;
	}

	public static Set<Item> getItems(final boolean currentTab) {
		if (!currentTab) return getItems();
		return getItems(new Filter<Item>() {
			@Override
			public boolean accept(final Item item) {
				return item.getComponent().getRelativeLocation().y != 0;
			}
		});
	}

	public static Set<Item> getItems(final int... ids) {
		Arrays.sort(ids);
		return getItems(new Filter<Item>() {
			@Override
			public boolean accept(final Item item) {
				return Arrays.binarySearch(ids, item.getId()) >= 0;
			}
		});
	}

	public static Item getItem(final int... ids) {
		final Set<Item> items = getItems(ids);
		if (items.isEmpty()) return null;
		return items.iterator().next();
	}

	public static Item getItem(final Filter<Item> filter) {
		final Set<Item> items = getItems(filter);
		if (items.isEmpty()) return null;
		return items.iterator().next();
	}

	public static Set<Item> getItems(final boolean currentTab, final Filter<Item> filter) {
		final Set<Item> items = getItems(currentTab);
		final Set<Item> set = new LinkedHashSet<>(items.size());
		for (final Item item : items) if (filter.accept(item)) set.add(item);
		return set;
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
