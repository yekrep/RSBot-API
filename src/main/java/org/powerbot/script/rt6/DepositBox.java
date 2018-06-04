package org.powerbot.script.rt6;

import java.util.ArrayList;
import java.util.List;

import org.powerbot.script.Condition;
import org.powerbot.script.Locatable;
import org.powerbot.script.StringUtils;
import org.powerbot.script.Tile;
import org.powerbot.script.Viewable;

/**
 * DepositBox
 */
public class DepositBox extends ItemQuery<Item> implements Viewable {
	public DepositBox(final ClientContext factory) {
		super(factory);//TODO: document class
	}

	private Interactive getBox() {
		return ctx.objects.select().id(Constants.DEPOSITBOX_ALTERNATIVES).viewable().nearest().poll();
	}

	/**
	 * Returns the absolute nearest bank for walking purposes. Do not use this to open the bank.
	 *
	 * @return the {@link org.powerbot.script.Locatable} of the nearest bank or {@link Tile#NIL}
	 * @see #open()
	 */
	public Locatable nearest() {
		final Locatable l = ctx.objects.select().id(Constants.DEPOSITBOX_ALTERNATIVES).nearest().poll();
		if (l.tile() != Tile.NIL) {
			return l;
		}
		return Tile.NIL;
	}

	/**
	 * Determines if a bank is present in the loaded region.
	 *
	 * @return {@code true} if a bank is present; otherwise {@code false}
	 */
	public boolean present() {
		return nearest() != Tile.NIL;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean inViewport() {
		return getBox().valid();
	}

	public boolean opened() {
		return ctx.widgets.widget(Constants.DEPOSITBOX_WIDGET).component(0).valid();
	}

	public boolean open() {
		if (opened()) {
			return true;
		}
		if (getBox().interact("Deposit")) {
			do {
				Condition.wait(new Condition.Check() {
					@Override
					public boolean poll() {
						return ctx.widgets.widget(13).component(0).valid() || opened();
					}
				}, 150, 15);
			} while (ctx.players.local().inMotion());
		}
		return opened();
	}

	/**
	 * Attempts to close the deposit box using mouse.
	 *
	 * @return {@code true} if the deposit box was successfully closed, {@code false} otherwise.
	 */
	public boolean close() {
		return close(false);
	}

	/**
	 * Attempts to close the deposit box using either hotkeys or mouse.
	 *
	 * @param hotkey Whether to use hotkeys to close the interface or not.
	 * @return {@code true} if the deposit box was successfully closed, {@code false} otherwise.
	 */
	public boolean close(final boolean hotkey) {
		if (!opened()) {
			return true;
		}
		final boolean interacted;
		if (hotkey) {
			interacted = ctx.input.send("{VK_ESCAPE}");
		} else {
			interacted = ctx.widgets.component(Constants.DEPOSITBOX_WIDGET, Constants.DEPOSITBOX_CLOSE).interact("Close");
		}
		return interacted && Condition.wait(new Condition.Check() {
			@Override
			public boolean poll() {
				return !opened();
			}
		}, 150);
	}

	@Override
	protected List<Item> get() {
		final Component c = ctx.widgets.component(Constants.DEPOSITBOX_WIDGET, Constants.DEPOSITBOX_ITEMS);
		if (c == null || !c.valid()) {
			return new ArrayList<Item>();
		}
		final Component[] components = c.components();
		final List<Item> items = new ArrayList<Item>(components.length);
		for (final Component i : components) {
			if (i.itemId() != -1) {
				items.add(new Item(ctx, i));
			}
		}
		return items;
	}

	public Item itemAt(final int index) {
		final Component c = ctx.widgets.component(Constants.DEPOSITBOX_WIDGET, Constants.DEPOSITBOX_ITEMS);
		if (c == null || !c.valid()) {
			return null;
		}
		final Component i = c.component(index);
		if (i != null && i.itemId() != -1) {
			return new Item(ctx, i);
		}
		return null;
	}

	public int indexOf(final int id) {
		final Component items = ctx.widgets.component(Constants.DEPOSITBOX_WIDGET, Constants.DEPOSITBOX_ITEMS);
		if (items == null || !items.valid()) {
			return -1;
		}
		final Component[] comps = items.components();
		for (int i = 0; i < comps.length; i++) {
			if (comps[i].itemId() == id) {
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
	 * @return {@code true} if the item was deposited, does not determine if amount was matched; otherwise {@code false}
	 */
	public boolean deposit(final int id, final Amount amount) {
		return deposit(id, amount.getValue());
	}

	public boolean deposit(final int id, final int amount) {
		if (!opened() || amount < 0) {
			return false;
		}
		final Item item = select().id(id).shuffle().poll();
		if (!item.valid()) {
			return false;
		}
		String action = "Deposit-" + amount;
		final int count = select().id(id).count(true);
		if (count == 1) {
			action = "Deposit-1";
		} else if (amount == 0 || count <= amount) {
			action = "Deposit-All";
		}
		final int cache = select().count(true);
		final Component component = item.component();
		if (amount != 0 && !containsAction(component, action)) {
			if (component.interact("Deposit-X") && Condition.wait(new Condition.Check() {
				@Override
				public boolean poll() {
					return isInputWidgetOpen();
				}
			})) {
				Condition.sleep();
				ctx.input.sendln(Integer.toString(amount));
			} else {
				return false;
			}
		} else {
			if (!component.interact(action)) {
				return false;
			}
		}
		return Condition.wait(new Condition.Check() {
			@Override
			public boolean poll() {
				return cache != select().count(true);
			}
		});
	}

	/**
	 * Deposits the inventory via the button.
	 *
	 * @return {@code true} if the button was clicked, not if the inventory is empty; otherwise {@code false}
	 */
	public boolean depositInventory() {
		return ctx.backpack.select().isEmpty() || ctx.widgets.component(Constants.DEPOSITBOX_WIDGET, Constants.DEPOSITBOX_DEPOSIT_INVENTORY).click();
	}

	/**
	 * Deposits equipment via the button.
	 *
	 * @return {@code true} if the button was clicked; otherwise {@code false}
	 */
	public boolean depositEquipment() {
		return ctx.widgets.component(Constants.DEPOSITBOX_WIDGET, Constants.DEPOSITBOX_DEPOSIT_EQUIPMENT).click();
	}

	/**
	 * Deposits familiar inventory via the button.
	 *
	 * @return {@code true} if the button was clicked; otherwise {@code false}
	 */
	public boolean depositFamiliar() {
		return ctx.widgets.component(Constants.DEPOSITBOX_WIDGET, Constants.DEPOSITBOX_DEPOSIT_FAMILIAR).click();
	}

	/**
	 * Deposits the money pouch via the button.
	 *
	 * @return {@code true} if the button was clicked; otherwise {@code false}
	 */
	public boolean depositMoneyPouch() {
		return ctx.backpack.moneyPouchCount() == 0 || ctx.widgets.component(Constants.DEPOSITBOX_WIDGET, Constants.DEPOSITBOX_DEPOSIT_POUCH).click();
	}

	private boolean containsAction(final Component c, final String action) {
		final String[] actions = c.actions();
		for (final String a : actions) {
			if (a != null && StringUtils.stripHtml(a).trim().equalsIgnoreCase(action)) {
				return true;
			}
		}
		return false;
	}

	private boolean isInputWidgetOpen() {
		return ctx.widgets.component(1469, 2).visible();
	}

	@Override
	public Item nil() {
		return new Item(ctx, -1, -1, null);
	}

	/**
	 * An enumeration providing standard bank amount options.
	 */
	public enum Amount {
		ONE(1), FIVE(5), TEN(10), ALL(0);

		private final int value;

		Amount(final int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}
}
