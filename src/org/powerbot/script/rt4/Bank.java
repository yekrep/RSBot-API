package org.powerbot.script.rt4;

import java.util.ArrayList;
import java.util.List;

import org.powerbot.script.Condition;

public class Bank extends ItemQuery<Item> {

	public Bank(final ClientContext ctx) {
		super(ctx);
	}

	@Override
	protected List<Item> get() {
		final List<Item> items = new ArrayList<Item>();
		if (!opened()) {
			return items;
		}
		for (final Component c : ctx.widgets.widget(Constants.BANK_WIDGET).component(Constants.BANK_ITEM_SCROLLBAR).components()) {
			final int id = c.itemId();
			if (id >= 0) {
				items.add(new Item(ctx, c, id, c.itemStackSize()));
			}
		}
		return items;
	}

	@Override
	public Item nil() {
		return new Item(ctx, null, -1, -1, -1);
	}


	public boolean opened() {
		return ctx.widgets.widget(Constants.BANK_WIDGET).component(Constants.BANK_WINDOW).visible();
	}

	public boolean close() {
		return !opened() || (ctx.widgets.widget(Constants.BANK_WIDGET).component(Constants.BANK_MASTER).component(Constants.BANK_CLOSE).interact("Close") && Condition.wait(new Condition.Check() {
			@Override
			public boolean poll() {
				return !opened();
			}
		}, 30, 10));
	}

	/**
	 * Withdraws an item with the provided id and amount.
	 *
	 * @param id     the id of the item
	 * @param amount the amount to withdraw
	 * @return <tt>true</tt> if the item was withdrew, does not determine if amount was matched; otherwise <tt>false</tt>
	 */
	public boolean withdraw(final int id, final Amount amount) {
		return withdraw(id, amount.getValue());
	}

	/**
	 * Withdraws an item with the provided id and amount.
	 *
	 * @param id     the id of the item
	 * @param amount the amount to withdraw
	 * @return <tt>true</tt> if the item was withdrew, does not determine if amount was matched; otherwise <tt>false</tt>
	 */
	public boolean withdraw(final int id, final int amount) {
		final Item item = select().id(id).shuffle().poll();
		if (!opened() || !item.valid() || amount < -1) {
			return false;
		}

		if (!ctx.widgets.scroll(
				ctx.widgets.widget(Constants.BANK_WIDGET).component(Constants.BANK_ITEM_SCROLLBAR),
				item.component,
				ctx.widgets.widget(Constants.BANK_WIDGET).component(Constants.BANK_SCROLLBAR)
		)) {
			return false;
		}
		final int count = select().id(id).count(true);
		final String action;
		if (count == 1 || amount == 1) {
			action = "Withdraw-1";
		} else if (amount == 0 || count <= amount) {
			action = "Withdraw-All";
		} else if (amount == 5 || amount == 10) {
			action = "Withdraw-" + amount;
		} else if (amount == -1) {
			action = "Withdraw-All-but-one";
		} else if (check(item, amount)) {
			action = "Withdraw-" + amount;
		} else {
			action = "Withdraw-X";
		}
		final int cache = ctx.inventory.select().count(true);
		if (!item.interact(action)) {
			return false;
		}
		if (action.endsWith("X")) {
			if (!Condition.wait(new Condition.Check() {
				@Override
				public boolean poll() {
					return ctx.widgets.widget(548).component(119).visible();
				}
			})) {
				return false;
			}
			Condition.sleep();
			ctx.input.sendln(amount + "");
		}
		return Condition.wait(new Condition.Check() {
			@Override
			public boolean poll() {
				return cache != ctx.inventory.select().count(true);
			}
		});
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

	/**
	 * Deposits an item with the provided id and amount.
	 *
	 * @param id     the id of the item
	 * @param amount the amount to deposit
	 * @return <tt>true</tt> if the item was deposited, does not determine if amount was matched; otherwise <tt>false</tt>
	 */
	public boolean deposit(final int id, final int amount) {
		if (!opened() || amount < 0) {
			return false;
		}
		final Item item = ctx.inventory.select().id(id).shuffle().poll();
		if (!item.valid()) {
			return false;
		}
		final int count = ctx.inventory.select().id(id).count(true);
		final String action;
		if (count == 1 || amount == 1) {
			action = "Deposit";
		} else if (amount == 0 || count <= amount) {
			action = "Deposit-All";
		} else if (amount == 5 || amount == 10) {
			action = "Deposit-" + amount;
		} else if (check(item, amount)) {
			action = "Deposit-" + amount;
		} else {
			action = "Deposit-X";
		}
		final int cache = ctx.inventory.select().count(true);
		if (!item.interact(action)) {
			return false;
		}
		if (action.endsWith("X")) {
			if (!Condition.wait(new Condition.Check() {
				@Override
				public boolean poll() {
					return ctx.widgets.widget(548).component(119).visible();
				}
			})) {
				return false;
			}
			Condition.sleep();
			ctx.input.sendln(amount + "");
		}
		return Condition.wait(new Condition.Check() {
			@Override
			public boolean poll() {
				return cache != ctx.inventory.select().count(true);
			}
		});
	}

	public boolean tabbed() {
		return ctx.varpbits.varpbit(Constants.BANK_TABS) != Constants.BANK_TABS_HIDDEN;
	}

	public boolean withdrawModeNoted() {
		return ctx.varpbits.varpbit(115) == 0x1;
	}

	public boolean withdrawModeNoted(final boolean noted) {
		return withdrawModeNoted() == noted || (ctx.widgets.widget(Constants.BANK_WIDGET).component(noted ? Constants.BANK_NOTE : Constants.BANK_ITEM).interact(noted ? "Note" : "Item") && Condition.wait(new Condition.Check() {
			@Override
			public boolean poll() {
				return withdrawModeNoted() == noted;
			}
		}, 30, 10));
	}

	public boolean depositInventory() {
		return ctx.widgets.widget(Constants.BANK_WIDGET).component(Constants.BANK_DEPOSIT_INVENTORY).interact("Deposit");
	}

	public boolean depositEquipment() {
		return ctx.widgets.widget(Constants.BANK_WIDGET).component(Constants.BANK_DEPOSIT_EQUIPMENT).interact("Deposit");
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

	private boolean check(final Item item, final int amt) {
		final String s = "-".concat(Integer.toString(amt));
		for (final String a : item.component.actions()) {
			if (a.endsWith(s)) {
				return true;
			}
		}
		return false;
	}
}
