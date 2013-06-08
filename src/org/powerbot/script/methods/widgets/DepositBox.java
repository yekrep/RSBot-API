package org.powerbot.script.methods.widgets;

import java.util.Arrays;

import org.powerbot.script.methods.Keyboard;
import org.powerbot.script.methods.Objects;
import org.powerbot.script.methods.Widgets;
import org.powerbot.script.methods.tabs.Inventory;
import org.powerbot.script.util.Delay;
import org.powerbot.script.util.Filter;
import org.powerbot.script.util.Filters;
import org.powerbot.script.util.Random;
import org.powerbot.script.util.Timer;
import org.powerbot.script.wrappers.Component;
import org.powerbot.script.wrappers.GameObject;
import org.powerbot.script.wrappers.Item;
import org.powerbot.script.wrappers.Widget;

public class DepositBox {
	public static final int[] DEPOSIT_BOX_IDS = new int[]{
			2045, 2133, 6396, 6402, 6404, 6417, 6418, 6453, 6457, 6478, 6836, 9398, 15985, 20228, 24995, 25937, 26969,
			32924, 32930, 32931, 34755, 36788, 39830, 45079, 66668, 70512, 73268
	};
	public static final int WIDGET = 11;
	public static final int COMPONENT_BUTTON_CLOSE = 15;
	public static final int COMPONENT_CONTAINER_ITEMS = 17;
	public static final int COMPONENT_BUTTON_DEPOSIT_INVENTORY = 19;
	public static final int COMPONENT_BUTTON_DEPOSIT_EQUIPMENT = 23;
	public static final int COMPONENT_BUTTON_DEPOSIT_FAMILIAR = 25;
	public static final int COMPONENT_BUTTON_DEPOSIT_POUCH = 21;

	public static boolean isOpen() {
		final Widget widget = Widgets.get(WIDGET);
		return widget != null && widget.isValid();
	}

	public static boolean open() {
		if (isOpen()) return true;
		GameObject object = Filters.nearest(Filters.id(Objects.getLoaded(), DEPOSIT_BOX_IDS));
		if (object.interact("Deposit")) {
			final Widget bankPin = Widgets.get(13);
			for (int i = 0; i < 20 && !isOpen() && !bankPin.isValid(); i++) Delay.sleep(200, 300);
		}
		return isOpen();
	}

	public static boolean close(final boolean wait) {
		if (!isOpen()) return true;
		final Component c = Widgets.get(WIDGET, COMPONENT_BUTTON_CLOSE);
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
		final Component c = Widgets.get(WIDGET, COMPONENT_CONTAINER_ITEMS);
		if (c == null || !c.isValid()) return new Item[0];
		final Component[] components = c.getChildren();
		Item[] items = new Item[components.length];
		int d = 0;
		for (final Component i : components) if (i.getItemId() != -1) items[d++] = new Item(i);
		return Arrays.copyOf(items, d);
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

	public static Item[] getItems(final Filter<Item> filter) {
		final Item[] items = getItems();
		final Item[] arr = new Item[items.length];
		int d = 0;
		for (final Item item : items) if (filter.accept(item)) arr[d++] = item;
		return Arrays.copyOf(arr, d);
	}

	public static Item getItemAt(final int index) {
		final Component c = Widgets.get(WIDGET, COMPONENT_CONTAINER_ITEMS);
		if (c == null || !c.isValid()) return null;
		final Component i = c.getChild(index);
		if (i != null && i.getItemId() != -1) return new Item(i);
		return null;
	}

	public static int indexOf(final int id) {
		final Component items = Widgets.get(WIDGET, COMPONENT_CONTAINER_ITEMS);
		if (items == null || !items.isValid()) return -1;
		final Component[] comps = items.getChildren();
		for (int i = 0; i < comps.length; i++) if (comps[i].getItemId() == id) return i;
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
		final Item[] items = getItems();
		for (final Item item : items) {
			if (stacks) count += item.getStackSize();
			else ++count;
		}
		return count;
	}

	public static int getCount(final int... ids) {
		return getCount(false, ids);
	}

	public static int getCount(final boolean stacks, final int... ids) {
		int count = 0;
		final Item[] items = getItems();
		for (final Item item : items) {
			for (final int id : ids) {
				if (item.getId() == id) {
					if (stacks) count += item.getStackSize();
					else ++count;
					break;
				}
			}
		}
		return count;
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

	public static boolean depositInventory() {
		final Component c = Widgets.get(WIDGET, COMPONENT_BUTTON_DEPOSIT_INVENTORY);
		if (c == null || !c.isValid()) return false;
		if (Inventory.isEmpty()) return true;
		final int inv = Inventory.getCount(true);
		if (c.click()) for (int i = 0; i < 25 && Inventory.getCount(true) == inv; i++) Delay.sleep(100, 200);
		return Inventory.getCount(true) != inv;
	}

	public static boolean depositEquipment() {
		final Component c = Widgets.get(WIDGET, COMPONENT_BUTTON_DEPOSIT_EQUIPMENT);
		return c != null && c.isValid() && c.click();
	}

	public static boolean depositFamiliar() {
		final Component c = Widgets.get(WIDGET, COMPONENT_BUTTON_DEPOSIT_FAMILIAR);
		return c != null && c.isValid() && c.click();
	}

	public static boolean depositPouch() {
		final Component c = Widgets.get(WIDGET, COMPONENT_BUTTON_DEPOSIT_POUCH);
		return c != null && c.isValid() && c.click();
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
