package org.powerbot.script.xenon.widgets;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Arrays;

import org.powerbot.script.xenon.Keyboard;
import org.powerbot.script.xenon.Settings;
import org.powerbot.script.xenon.Widgets;
import org.powerbot.script.xenon.tabs.Inventory;
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
	public static final int COMPONENT_SCROLL_BAR = 116;
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
		if (c != null && c.isValid() && c.click(true)) {
			final Timer timer = new Timer(800);
			while (timer.isRunning() && getCurrentTab() != index) Delay.sleep(15);
			return getCurrentTab() == index;
		}
		return false;
	}

	public static Item getTabItem(final int index) {
		final Component c = Widgets.get(WIDGET, 63 - (index * 2));
		if (c != null && c.isValid()) return new Item(c);
		return null;
	}

	public static boolean withdraw(final int id, final int amount) {
		final Item item = getItem(id);
		if (item == null) return false;
		final Component container = Widgets.get(WIDGET, COMPONENT_ITEMS);
		if (container == null || !container.isValid()) return false;

		final Component c = item.getComponent();
		Point p = c.getRelativeLocation();
		if (p.y == 0) for (int i = 0; i < 5 && getCurrentTab() != 0; i++) if (!setCurrentTab(0)) Delay.sleep(100, 200);
		if ((p = c.getRelativeLocation()).y == 0) return false;
		final Rectangle bounds = container.getViewportRect();
		final Component scroll = Widgets.get(WIDGET, COMPONENT_SCROLL_BAR);
		if (scroll == null) return false;
		if (!bounds.contains(c.getBoundingRect()) && !Widgets.scroll(c, scroll)) return false;
		if (!bounds.contains(c.getBoundingRect())) return false;

		String action = "Withdraw-" + amount;
		if (amount == 0 ||
				(item.getStackSize() <= amount && amount != 1 && amount != 5 && amount != 10)) action = "Withdraw-All";
		else if (amount == -1 || amount == (item.getStackSize() - 1)) action = "Withdraw-All but one";

		final int inv = Inventory.getCount(true);
		if (containsAction(c, action)) {
			if (!c.interact(action)) return false;
		} else {
			if (!c.interact("Withdraw-X")) return false;
			for (int i = 0; i < 20 && !isInputWidgetOpen(); i++) Delay.sleep(100, 200);
			if (!isInputWidgetOpen()) return false;
			Delay.sleep(200, 800);
			Keyboard.sendln(amount + "");
		}
		for (int i = 0; i < 25 && Inventory.getCount(true) == inv; i++) Delay.sleep(100, 200);
		return Inventory.getCount(true) != inv || Inventory.isFull();
	}

	public static boolean deposit(final int id, final int amount) {
		if (!isOpen() || amount < 0) return false;
		final Item item = Inventory.getItem(id);
		if (item == null) return false;
		String action = "Deposit-" + amount;
		final int c = Inventory.getCount(true, id);
		if (c == 1) action = "Depoist";
		else if (c <= amount || amount == 0) {
			action = "Deposit-All";
		}

		final Component comp = item.getComponent();
		final int inv = Inventory.getCount(true);
		if (containsAction(comp, action)) {
			if (!comp.interact(action)) return false;
		} else {
			if (!comp.interact("Withdraw-X")) return false;
			for (int i = 0; i < 20 && !isInputWidgetOpen(); i++) Delay.sleep(100, 200);
			if (!isInputWidgetOpen()) return false;
			Delay.sleep(200, 800);
			Keyboard.sendln(amount + "");
		}
		for (int i = 0; i < 25 && Inventory.getCount(true) == inv; i++) Delay.sleep(100, 200);
		return Inventory.getCount(true) != inv;
	}

	private static boolean containsAction(final Component c, final String action) {
		final String[] actions = c.getActions();
		if (action == null) return false;
		for (final String a : actions) if (a != null && a.matches("^" + action + "(<.*>)?$")) return true;
		return false;
	}

	private static boolean isInputWidgetOpen() {
		final Component child = Widgets.get(752, 3);
		return child != null && child.isValid() && child.isOnScreen();
	}
}
