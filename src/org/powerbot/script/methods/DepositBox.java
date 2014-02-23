package org.powerbot.script.methods;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.powerbot.script.lang.ItemQuery;
import org.powerbot.script.util.Condition;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.Component;
import org.powerbot.script.wrappers.Interactive;
import org.powerbot.script.wrappers.Item;
import org.powerbot.script.wrappers.Locatable;
import org.powerbot.script.wrappers.Tile;

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


	private Interactive getBox() {
		return ctx.objects.select().id(DEPOSIT_BOX_IDS).select(Interactive.areInViewport()).nearest().poll();
	}

	/**
	 * Returns the absolute nearest bank for walking purposes. Do not use this to open the bank.
	 *
	 * @return the {@link org.powerbot.script.wrappers.Locatable} of the nearest bank or {@link Tile#NIL}
	 * @see #open()
	 */
	public Locatable getNearest() {
		return ctx.objects.select().id(DEPOSIT_BOX_IDS).nearest().poll().getLocation();
	}

	/**
	 * Determines if a bank is present in the loaded region.
	 *
	 * @return <tt>true</tt> if a bank is present; otherwise <tt>false</tt>
	 */
	public boolean isPresent() {
		return getNearest() != Tile.NIL;
	}

	/**
	 * Determines if a bank is in the viewport.
	 *
	 * @return <tt>true</tt> if a bank is in the viewport; otherwise <tt>false</tt>
	 */
	public boolean isInViewport() {
		return getBox().isValid();
	}

	/**
	 * @see {@link #isInViewport()}
	 */
	@Deprecated
	public boolean isOnScreen() {
		return isInViewport();
	}

	public boolean isOpen() {
		return ctx.widgets.get(WIDGET).isValid();
	}

	public boolean open() {
		if (isOpen()) {
			return true;
		}
		if (getBox().interact("Deposit")) {
			do {
				Condition.wait(new Callable<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return ctx.widgets.get(13).isValid() || isOpen();
					}
				}, 150, 15);
			} while (ctx.players.local().isInMotion());
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
				}, 150, 10);
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

	/**
	 * Deposits an item with the provided id and amount.
	 *
	 * @param id     the id of the item
	 * @param amount the amount to deposit
	 * @return <tt>true</tt> if the item was deposited, does not determine if amount was matched; otherwise <tt>false</tt>
	 */
	public boolean deposit(final int id, final Amount amount) {
		return deposit(id, amount.getValue());
	}

	public boolean deposit(final int id, final int amount) {
		if (!isOpen() || amount < 0) {
			return false;
		}
		final Item item = select().id(id).shuffle().poll();
		if (!item.isValid()) {
			return false;
		}
		String action = "Deposit-" + amount;
		final int count = select().id(id).count(true);
		if (count == 1) {
			action = "Deposit";
		} else if (amount == 0 || count <= amount) {
			action = "Deposit-All";
		}
		final int cache = select().count(true);
		final Component component = item.getComponent();
		System.out.print(action + " " + containsAction(component, action));
		if (!containsAction(component, action)) {
			if (component.interact("Deposit-X") && Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return isInputWidgetOpen();
				}
			})) {
				sleep(Random.nextInt(800, 1200));
				ctx.keyboard.sendln(amount + "");
			} else {
				return false;
			}
		} else {
			if (!component.interact(action)) {
				return false;
			}
		}
		return Condition.wait(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return cache != select().count(true);
			}
		});
	}

	/**
	 * Deposits the inventory via the button.
	 *
	 * @return <tt>true</tt> if the button was clicked, not if the inventory is empty; otherwise <tt>false</tt>
	 */
	public boolean depositInventory() {
		return ctx.backpack.select().isEmpty() || ctx.widgets.get(WIDGET, COMPONENT_BUTTON_DEPOSIT_INVENTORY).click();
	}

	/**
	 * Deposits equipment via the button.
	 *
	 * @return <tt>true</tt> if the button was clicked; otherwise <tt>false</tt>
	 */
	public boolean depositEquipment() {
		return ctx.widgets.get(WIDGET, COMPONENT_BUTTON_DEPOSIT_EQUIPMENT).click();
	}

	/**
	 * Deposits familiar inventory via the button.
	 *
	 * @return <tt>true</tt> if the button was clicked; otherwise <tt>false</tt>
	 */
	public boolean depositFamiliar() {
		return ctx.widgets.get(WIDGET, COMPONENT_BUTTON_DEPOSIT_FAMILIAR).click();
	}

	/**
	 * Deposits the money pouch via the button.
	 *
	 * @return <tt>true</tt> if the button was clicked; otherwise <tt>false</tt>
	 */
	public boolean depositMoneyPouch() {
		return ctx.backpack.getMoneyPouch() == 0 || ctx.widgets.get(WIDGET, COMPONENT_BUTTON_DEPOSIT_POUCH).click();
	}

	private boolean containsAction(final Component c, final String action) {
		final String[] actions = c.getActions();
		for (final String a : actions) {
			if (a != null && a.trim().equalsIgnoreCase(action)) {
				return true;
			}
		}
		return false;
	}

	private boolean isInputWidgetOpen() {
		return ctx.widgets.get(1469, 2).isVisible();
	}

	@Override
	public Item getNil() {
		return new Item(ctx, -1, -1, null);
	}

	/**
	 * An enumeration providing standard bank amount options.
	 */
	public static enum Amount {
		ONE(1), FIVE(5), TEN(10), ALL_BUT_ONE(-1), ALL(0);

		private final int value;

		private Amount(final int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}
}
