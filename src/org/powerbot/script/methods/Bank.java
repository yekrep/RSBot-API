package org.powerbot.script.methods;

import org.powerbot.script.util.Delay;
import org.powerbot.script.util.Filter;
import org.powerbot.script.util.Random;
import org.powerbot.script.util.Timer;
import org.powerbot.script.wrappers.Component;
import org.powerbot.script.wrappers.GameObject;
import org.powerbot.script.wrappers.Interactive;
import org.powerbot.script.wrappers.Item;
import org.powerbot.script.wrappers.Npc;
import org.powerbot.script.wrappers.Widget;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Arrays;

public class Bank extends ClientLink {
	public static final int[] BANK_NPC_IDS = new int[]{
			44, 45, 166, 494, 495, 496, 497, 498, 499, 553, 909, 953, 958, 1036, 1360, 1702, 2163, 2164, 2354, 2355,
			2568, 2569, 2570, 2617, 2618, 2619, 2718, 2759, 3046, 3198, 3199, 3293, 3416, 3418, 3824, 4456, 4457,
			4458, 4459, 4519, 4907, 5257, 5258, 5259, 5260, 5488, 5776, 5777, 5901, 6200, 6362, 7049, 7050, 7605,
			8948, 9710, 13932, 14707, 14923, 14924, 14925, 15194, 16603, 16602
	};
	public static final int[] BANK_BOOTH_IDS = new int[]{
			782, 2213, 3045, 5276, 6084, 10517, 11338, 11758, 12759, 12798, 12799, 14369, 14370,
			16700, 19230, 20325, 20326, 20327, 20328, 22819, 24914, 25808, 26972, 29085, 34752, 35647,
			36262, 36786, 37474, 49018, 49019, 52397, 52589, 76274, 66665, 66666, 66667, 69024, 69023, 69022
	};
	public static final int[] BANK_COUNTER_IDS = new int[]{
			42217, 42377, 42378, 2012
	};
	public static final int[] BANK_CHEST_IDS = new int[]{
			2693, 4483, 8981, 12308, 14382, 20607, 21301, 27663, 42192, 57437, 62691, 83634, 81756
	};
	public static final int WIDGET = 762;
	public static final int COMPONENT_BUTTON_CLOSE = 45;
	public static final int COMPONENT_CONTAINER_ITEMS = 95;
	public static final int COMPONENT_BUTTON_WITHDRAW_MODE = 20;
	public static final int COMPONENT_BUTTON_DEPOSIT_INVENTORY = 34;
	public static final int COMPONENT_BUTTON_DEPOSIT_EQUIPMENT = 38;
	public static final int COMPONENT_BUTTON_DEPOSIT_FAMILIAR = 40;
	public static final int COMPONENT_SCROLL_BAR = 116;
	public static final int SETTING_BANK_STATE = 110;
	public static final int SETTING_WITHDRAW_MODE = 160;

	public Bank(ClientFactory factory) {
		super(factory);
	}

	public boolean isOpen() {
		final Widget widget = ctx.widgets.get(WIDGET);
		return widget != null && widget.isValid();
	}

	public boolean open() {
		if (isOpen()) {
			return true;
		}
		int count = 0;
		final Interactive[] interactives = {
				ctx.npcs.select().id(BANK_NPC_IDS).nearest().first(),
				ctx.objects.select().id(BANK_BOOTH_IDS).nearest().first(),
				ctx.objects.select().id(BANK_COUNTER_IDS).nearest().first(),
				ctx.objects.select().id(BANK_CHEST_IDS).nearest().first(),
		};
		for (int i = 0; i < interactives.length; i++) {
			if (interactives[i] != null && interactives[i].isOnScreen()) {
				interactives[count++] = interactives[i];
			}
		}
		if (count == 0) {
			return false;
		}
		final Interactive interactive = interactives[Random.nextInt(0, count)];
		final int id;
		if (interactive instanceof Npc) {
			id = ((Npc) interactive).getId();
		} else if (interactive instanceof GameObject) {
			id = ((GameObject) interactive).getId();
		} else {
			id = -1;
		}
		if (id == -1) {
			return false;
		}
		int index = -1;
		final int[][] ids = {BANK_NPC_IDS, BANK_BOOTH_IDS, BANK_COUNTER_IDS, BANK_CHEST_IDS};
		for (int i = 0; i < ids.length; i++) {
			Arrays.sort(ids[i]);
			if (Arrays.binarySearch(ids[i], id) >= 0) {
				index = i;
				break;
			}
		}
		if (index == -1) {
			return false;
		}
		final String[] actions = {"Bank", "Bank", null, "Bank"};
		final String[] options = {null, "Bank booth", null, "Counter"};
		if (actions[index] == null) {
			interactive.hover();
			actions[index] = ctx.menu.indexOf("Open") != -1 ? "Open" : ctx.menu.indexOf("Use") != -1 ? "Use" : null;
			if (actions[index] == null) {
				return false;
			}
		}
		if (interactive.interact(actions[index], options[index])) {
			final Widget bankPin = ctx.widgets.get(13);
			for (int i = 0; i < 20 && !isOpen() && !bankPin.isValid(); i++) {
				Delay.sleep(200, 300);
			}
		}
		return isOpen();
	}

	public boolean close(final boolean wait) {
		if (!isOpen()) {
			return true;
		}
		final Component c = ctx.widgets.get(WIDGET, COMPONENT_BUTTON_CLOSE);
		if (c == null) {
			return false;
		}
		if (c.isValid() && c.interact("Close")) {
			if (!wait) {
				return true;
			}
			final Timer t = new Timer(Random.nextInt(1000, 2000));
			while (t.isRunning() && isOpen()) {
				Delay.sleep(100);
			}
			return !isOpen();
		}
		return false;
	}

	public boolean close() {
		return close(true);
	}

	public Item[] getItems() {
		final Component c = ctx.widgets.get(WIDGET, COMPONENT_CONTAINER_ITEMS);
		if (c == null || !c.isValid()) {
			return new Item[0];
		}
		final Component[] components = c.getChildren();
		Item[] items = new Item[components.length];
		int d = 0;
		for (final Component i : components) {
			if (i.getItemId() != -1) {
				items[d++] = new Item(ctx, i);
			}
		}
		return Arrays.copyOf(items, d);
	}

	public Item[] getItems(final Filter<Item> filter) {
		return getItems(false, filter);
	}

	public Item[] getItems(final boolean currentTab) {
		if (!currentTab) {
			return getItems();
		}
		return getItems(new Filter<Item>() {
			@Override
			public boolean accept(final Item item) {
				return item.getComponent().getRelativeLocation().y != 0;
			}
		});
	}

	public Item[] getItems(final int... ids) {
		Arrays.sort(ids);
		return getItems(new Filter<Item>() {
			@Override
			public boolean accept(final Item item) {
				return Arrays.binarySearch(ids, item.getId()) >= 0;
			}
		});
	}

	public Item getItem(final int... ids) {
		final Item[] items = getItems(ids);
		return items.length > 0 ? items[0] : null;
	}

	public Item getItem(final Filter<Item> filter) {
		return getItem(false, filter);
	}

	public Item getItem(final boolean currentTab, final Filter<Item> filter) {
		final Item[] items = getItems(currentTab, filter);
		return items.length > 0 ? items[0] : null;
	}

	public Item[] getItems(final boolean currentTab, final Filter<Item> filter) {
		final Item[] items = getItems(currentTab);
		final Item[] arr = new Item[items.length];
		int d = 0;
		for (final Item item : items) {
			if (filter.accept(item)) {
				arr[d++] = item;
			}
		}
		return Arrays.copyOf(arr, d);
	}

	public Item getItemAt(final int index) {
		final Component c = ctx.widgets.get(WIDGET, COMPONENT_CONTAINER_ITEMS);
		if (c == null || !c.isValid()) {
			return null;
		}
		final Component i = c.getChild(index);
		if (i != null && i.getItemId() != -1) {
			return new Item(ctx, i);
		}
		return null;
	}

	public int indexOf(final int id) {
		final Component items = ctx.widgets.get(WIDGET, COMPONENT_CONTAINER_ITEMS);
		if (items == null || !items.isValid()) {
			return -1;
		}
		final Component[] comps = items.getChildren();
		for (int i = 0; i < comps.length; i++) {
			if (comps[i].getItemId() == id) {
				return i;
			}
		}
		return -1;
	}

	public boolean contains(final int id) {
		return indexOf(id) != -1;
	}

	public boolean containsAll(final int... ids) {
		for (final int id : ids) {
			if (indexOf(id) == -1) {
				return false;
			}
		}
		return true;
	}

	public boolean containsOneOf(final int... ids) {
		for (final int id : ids) {
			if (indexOf(id) != -1) {
				return true;
			}
		}
		return false;
	}

	public int getCount() {
		return getCount(false);
	}

	public int getCount(final boolean stacks) {
		int count = 0;
		final Item[] items = getItems();
		for (final Item item : items) {
			if (stacks) {
				count += item.getStackSize();
			} else {
				++count;
			}
		}
		return count;
	}

	public int getCount(final int... ids) {
		return getCount(false, ids);
	}

	public int getCount(final boolean stacks, final int... ids) {
		int count = 0;
		final Item[] items = getItems();
		for (final Item item : items) {
			for (final int id : ids) {
				if (item.getId() == id) {
					if (stacks) {
						count += item.getStackSize();
					} else {
						++count;
					}
					break;
				}
			}
		}
		return count;
	}

	public int getCurrentTab() {
		return ((ctx.settings.get(SETTING_BANK_STATE) >>> 24) - 136) / 8;
	}

	public boolean setCurrentTab(final int index) {
		final Component c = ctx.widgets.get(WIDGET, 63 - (index * 2));
		if (c != null && c.isValid() && c.click(true)) {
			final Timer timer = new Timer(800);
			while (timer.isRunning() && getCurrentTab() != index) {
				Delay.sleep(15);
			}
			return getCurrentTab() == index;
		}
		return false;
	}

	public Item getTabItem(final int index) {
		final Component c = ctx.widgets.get(WIDGET, 63 - (index * 2));
		if (c != null && c.isValid()) {
			return new Item(ctx, c);
		}
		return null;
	}

	public boolean withdraw(final int id, final int amount) {
		final Item item = getItem(id);
		if (item == null) {
			return false;
		}
		final Component container = ctx.widgets.get(WIDGET, COMPONENT_CONTAINER_ITEMS);
		if (container == null || !container.isValid()) {
			return false;
		}

		final Component c = item.getComponent();
		Point p = c.getRelativeLocation();
		if (p.y == 0) {
			for (int i = 0; i < 5 && getCurrentTab() != 0; i++) {
				if (!setCurrentTab(0)) {
					Delay.sleep(100, 200);
				}
			}
		}
		if (c.getRelativeLocation().y == 0) {
			return false;
		}
		final Rectangle bounds = container.getViewportRect();
		final Component scroll = ctx.widgets.get(WIDGET, COMPONENT_SCROLL_BAR);
		if (scroll == null) {
			return false;
		}
		if (!bounds.contains(c.getBoundingRect()) && !ctx.widgets.scroll(c, scroll)) {
			return false;
		}
		if (!bounds.contains(c.getBoundingRect())) {
			return false;
		}

		String action = "Withdraw-" + amount;
		if (amount == 0 ||
				(item.getStackSize() <= amount && amount != 1 && amount != 5 && amount != 10)) {
			action = "Withdraw-All";
		} else if (amount == -1 || amount == (item.getStackSize() - 1)) {
			action = "Withdraw-All but one";
		}

		final int inv = ctx.inventory.getCount(true);
		if (containsAction(c, action)) {
			if (!c.interact(action)) {
				return false;
			}
		} else {
			if (!c.interact("Withdraw-X")) {
				return false;
			}
			for (int i = 0; i < 20 && !isInputWidgetOpen(); i++) {
				Delay.sleep(100, 200);
			}
			if (!isInputWidgetOpen()) {
				return false;
			}
			Delay.sleep(200, 800);
			ctx.keyboard.sendln(amount + "");
		}
		for (int i = 0; i < 25 && ctx.inventory.getCount(true) == inv; i++) {
			Delay.sleep(100, 200);
		}
		return ctx.inventory.getCount(true) != inv || ctx.inventory.isFull();
	}

	public boolean deposit(final int id, final int amount) {
		if (!isOpen() || amount < 0) {
			return false;
		}
		final Item item = ctx.inventory.select().id(id).first();
		if (item == null) {
			return false;
		}
		String action = "Deposit-" + amount;
		final int c = ctx.inventory.getCount(true, id);
		if (c == 1) {
			action = "Depoist";
		} else if (c <= amount || amount == 0) {
			action = "Deposit-All";
		}

		final Component comp = item.getComponent();
		final int inv = ctx.inventory.getCount(true);
		if (containsAction(comp, action)) {
			if (!comp.interact(action)) {
				return false;
			}
		} else {
			if (!comp.interact("Withdraw-X")) {
				return false;
			}
			for (int i = 0; i < 20 && !isInputWidgetOpen(); i++) {
				Delay.sleep(100, 200);
			}
			if (!isInputWidgetOpen()) {
				return false;
			}
			Delay.sleep(200, 800);
			ctx.keyboard.sendln(amount + "");
		}
		for (int i = 0; i < 25 && ctx.inventory.getCount(true) == inv; i++) {
			Delay.sleep(100, 200);
		}
		return ctx.inventory.getCount(true) != inv;
	}

	public boolean depositInventory() {
		final Component c = ctx.widgets.get(WIDGET, COMPONENT_BUTTON_DEPOSIT_INVENTORY);
		if (c == null || !c.isValid()) {
			return false;
		}
		if (ctx.inventory.isEmpty()) {
			return true;
		}
		return c.click();
	}

	public boolean depositEquipment() {
		final Component c = ctx.widgets.get(WIDGET, COMPONENT_BUTTON_DEPOSIT_EQUIPMENT);
		return c != null && c.isValid() && c.click();
	}

	public boolean depositFamiliar() {
		final Component c = ctx.widgets.get(WIDGET, COMPONENT_BUTTON_DEPOSIT_FAMILIAR);
		return c != null && c.isValid() && c.click();
	}

	public boolean setWithdrawMode(final boolean noted) {
		if (isWithdrawModeNoted() != noted) {
			final Component c = ctx.widgets.get(WIDGET, COMPONENT_BUTTON_WITHDRAW_MODE);
			if (c != null && c.isValid() && c.click(true)) {
				for (int i = 0; i < 20 && isWithdrawModeNoted() != noted; i++) {
					Delay.sleep(100, 200);
				}
			}
		}
		return isWithdrawModeNoted() == noted;
	}

	public boolean isWithdrawModeNoted() {
		return ctx.settings.get(SETTING_WITHDRAW_MODE) == 0x1;
	}

	private boolean containsAction(final Component c, final String action) {
		final String[] actions = c.getActions();
		if (action == null) {
			return false;
		}
		for (final String a : actions) {
			if (a != null && a.matches("^" + action + "(<.*>)?$")) {
				return true;
			}
		}
		return false;
	}

	private boolean isInputWidgetOpen() {
		final Component child = ctx.widgets.get(752, 3);
		return child != null && child.isValid() && child.isOnScreen();
	}
}
