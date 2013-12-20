package org.powerbot.script.methods;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.powerbot.script.lang.ItemQuery;
import org.powerbot.script.util.Condition;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.Component;
import org.powerbot.script.wrappers.GameObject;
import org.powerbot.script.wrappers.Item;
import org.powerbot.script.wrappers.Widget;

public class DepositBox extends ItemQuery<Item> {
	public static final int[] DEPOSIT_BOX_IDS = new int[]{
			2045, 2133, 6396, 6402, 6404, 6417, 6418, 6453, 6457, 6478, 6836, 9398, 15985, 20228, 24995, 25937, 26969,
			32924, 32930, 32931, 34755, 36788, 39830, 45079, 66668, 70512, 73268, 79036
	};
	public static final int WIDGET = 11;
	public static final int COMPONENT_BUTTON_CLOSE = 14;
	public static final int COMPONENT_CONTAINER_ITEMS = 15;
	public static final int COMPONENT_BUTTON_DEPOSIT_INVENTORY = 17;
	public static final int COMPONENT_BUTTON_DEPOSIT_EQUIPMENT = 21;
	public static final int COMPONENT_BUTTON_DEPOSIT_FAMILIAR = 23;
	public static final int COMPONENT_BUTTON_DEPOSIT_POUCH = 19;

	public DepositBox(final MethodContext factory) {
		super(factory);//TODO: document class
	}

	public boolean isOpen() {
		final Widget widget = ctx.widgets.get(WIDGET);
		return widget != null && widget.isValid();
	}

	public boolean open() {
		if (isOpen()) {
			return true;
		}
		for (final GameObject object : ctx.objects.select().id(DEPOSIT_BOX_IDS).nearest().limit(1)) {
			if (object.interact("Deposit")) {
				final Widget bankPin = ctx.widgets.get(13);
				for (int i = 0; i < 20 && !isOpen() && !bankPin.isValid(); i++) {
					sleep(200, 300);
				}
			}
		}
		return isOpen();
	}

	public boolean close(final boolean wait) {
		if (!isOpen()) {
			return true;
		}
		final Component c = ctx.widgets.get(WIDGET, COMPONENT_BUTTON_CLOSE);
		if (c.interact("Close")) {
			if (wait) {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return !isOpen();
					}
				}, Random.nextInt(100, 200), 10);
			}
		}
		return !isOpen();
	}

	public boolean close() {
		return close(true);
	}

	@Override
	protected List<Item> get() {
		final Component c = ctx.widgets.get(WIDGET, COMPONENT_CONTAINER_ITEMS);
		if (c == null || !c.isValid()) {
			return new ArrayList<Item>();
		}
		final Component[] components = c.getChildren();
		final List<Item> items = new ArrayList<Item>(components.length);
		for (final Component i : components) {
			if (i.getItemId() != -1) {
				items.add(new Item(ctx, i));
			}
		}
		return items;
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

	public boolean deposit(final int id, final int amount) {
		if (!isOpen() || amount < 0) {
			return false;
		}

		for (final Item item : ctx.backpack.select().id(id).limit(1)) {
			String action = "Deposit-" + amount;
			final int c = ctx.backpack.select().id(id).count(true);
			if (c == 1) {
				action = "Depoist";
			} else if (c <= amount || amount == 0) {
				action = "Deposit-All";
			}

			final Component comp = item.getComponent();
			final int inv = ctx.backpack.select().count(true);
			if (containsAction(comp, action)) {
				if (!comp.interact(action)) {
					return false;
				}
			} else {
				if (!comp.interact("Withdraw-X")) {
					return false;
				}
				for (int i = 0; i < 20 && !isInputWidgetOpen(); i++) {
					sleep(100, 200);
				}
				if (!isInputWidgetOpen()) {
					return false;
				}
				sleep(200, 800);
				ctx.keyboard.sendln(amount + "");
			}
			for (int i = 0; i < 25 && ctx.backpack.select().count(true) == inv; i++) {
				sleep(100, 200);
			}
			return ctx.backpack.select().count(true) != inv;
		}

		return false;
	}

	public boolean depositInventory() {
		final Component c = ctx.widgets.get(WIDGET, COMPONENT_BUTTON_DEPOSIT_INVENTORY);
		if (c == null || !c.isValid()) {
			return false;
		}
		if (ctx.backpack.isEmpty()) {
			return true;
		}
		final int inv = ctx.backpack.select().count(true);
		if (c.click()) {
			for (int i = 0; i < 25 && ctx.backpack.select().count(true) == inv; i++) {
				sleep(100, 200);
			}
		}
		return ctx.backpack.select().count(true) != inv;
	}

	public boolean depositEquipment() {
		final Component c = ctx.widgets.get(WIDGET, COMPONENT_BUTTON_DEPOSIT_EQUIPMENT);
		return c != null && c.isValid() && c.click();
	}

	public boolean depositFamiliar() {
		final Component c = ctx.widgets.get(WIDGET, COMPONENT_BUTTON_DEPOSIT_FAMILIAR);
		return c != null && c.isValid() && c.click();
	}

	public boolean depositPouch() {
		final Component c = ctx.widgets.get(WIDGET, COMPONENT_BUTTON_DEPOSIT_POUCH);
		return c != null && c.isValid() && c.click();
	}

	private boolean containsAction(final Component c, String action) {
		action = action.toLowerCase();
		final String[] actions = c.getActions();
		for (final String a : actions) {
			if (a != null && a.toLowerCase().contains(action)) {
				return true;
			}
		}
		return false;
	}

	private boolean isInputWidgetOpen() {
		final Component child = ctx.widgets.get(752, 3);
		return child != null && child.isValid() && child.isOnScreen();
	}

	@Override
	public Item getNil() {
		return new Item(ctx, -1, -1, null);
	}
}
