package org.powerbot.script.rt4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.MenuCommand;

/**
 * Bank
 * A utility class for withdrawing and depositing items, opening and closing the bank, and finding the closest usable bank.
 */
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
		for (final Component c : ctx.widgets.widget(Constants.BANK_WIDGET).component(Constants.BANK_ITEMS).components()) {
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
		return ctx.widgets.widget(Constants.BANK_WIDGET).component(Constants.BANK_MASTER).visible();
	}

	public boolean close() {
		return !opened() || (ctx.widgets.widget(Constants.BANK_WIDGET).component(Constants.BANK_MASTER).component(Constants.BANK_CLOSE).click(true) && Condition.wait(new Condition.Check() {
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
	 * @return <tt>true</tt> if the item was withdrawn, does not determine if amount was matched; otherwise, <tt>false</tt>
	 */
	public boolean withdraw(final int id, final Amount amount) {
		return withdraw(id, amount.getValue());
	}

	/**
	 * Withdraws an item with the provided id and amount.
	 *
	 * @param id     the id of the item
	 * @param amount the amount to withdraw
	 * @return <tt>true</tt> if the item was withdrawn, does not determine if amount was matched; otherwise, <tt>false</tt>
	 */
	public boolean withdraw(final int id, final int amount) {
		return withdraw(select().id(id).poll(), amount);
	}

	/**
	 * Withdraws an item with the provided item and amount.
	 *
	 * @param item   the item instance
	 * @param amount the amount to withdraw
	 * @return <tt>true</tt> if the item was withdrawn, does not determine if amount was matched; otherwise, <tt>false</tt>
	 */
	public boolean withdraw(final Item item, final int amount) {
		if (!opened() || !item.valid() || amount < -1) {
			return false;
		}

		if (!ctx.widgets.scroll(
				ctx.widgets.widget(Constants.BANK_WIDGET).component(Constants.BANK_ITEMS),
				item.component,
				ctx.widgets.widget(Constants.BANK_WIDGET).component(Constants.BANK_SCROLLBAR)
		)) {
			return false;
		}
		final int count = select().id(item.id()).count(true);
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
		if (item.contains(ctx.input.getLocation())) {
			if (!(ctx.menu.click(new Filter<MenuCommand>() {
				@Override
				public boolean accept(final MenuCommand command) {
					return command.action.equalsIgnoreCase(action);
				}
			}) || item.interact(action))) {
				return false;
			}
		} else if (!item.interact(action)) {
			return false;
		}
		if (action.endsWith("X")) {
			if (!Condition.wait(new Condition.Check() {
				@Override
				public boolean poll() {
					return ctx.widgets.widget(162).component(33).visible();
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
		if (item.contains(ctx.input.getLocation())) {
			if (!(ctx.menu.click(new Filter<MenuCommand>() {
				@Override
				public boolean accept(final MenuCommand command) {
					return command.action.equalsIgnoreCase(action);
				}
			}) || item.interact(action))) {
				return false;
			}
		} else if (!item.interact(action)) {
			return false;
		}
		if (action.endsWith("X")) {
			if (!Condition.wait(new Condition.Check() {
				@Override
				public boolean poll() {
					return ctx.widgets.widget(162).component(33).visible();
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
	 * Deposits the players inventory excluding the specified ids.
	 *
	 * @param ids the ids of the items to ignore when depositing
	 * @return <tt>true</tt> if the items were deposited, does not determine if amount was matched; otherwise <tt>false</tt>
	 */
	public boolean depositAllExcept(final int... ids) {
		final boolean[] ret = {true};
		final int count = ctx.inventory.select().count();

		ctx.inventory.select(new Filter<Item>() {
			@Override
			public boolean accept(final Item item) {
				for (final int id : ids) {
					if (item.id() == id) {
						return false;
					}
				}

				return true;
			}
		});

		if (count != ctx.inventory.count()) {
			ctx.inventory.shuffle().each(new Filter<Item>() {
				@Override
				public boolean accept(final Item item) {
					if (!deposit(item.id(), Amount.ALL)) {
						ret[0] = false;
					}

					return true;
				}
			});
		} else {
			return depositInventory();
		}

		return ret[0];
	}

	/**
	 * Deposits an item with the provided name and amount.
	 *
	 * @param name   the name of the item
	 * @param amount the amount to deposit
	 * @return <tt>true</tt> if the item was deposited, does not determine if amount was matched; otherwise <tt>false</tt>
	 */
	public boolean deposit(final String name, final Amount amount) {
		return deposit(name, amount.getValue());
	}

	/**
	 * Deposits an item with the provided name and amount.
	 *
	 * @param name   the name of the item
	 * @param amount the amount to deposit
	 * @return <tt>true</tt> if the item was deposited, does not determine if amount was matched; otherwise <tt>false</tt>
	 */
	public boolean deposit(final String name, final int amount) {
		return deposit(ctx.inventory.select().name(name).peek().id(), amount);
	}

	/**
	 * Deposits the players inventory excluding the specified item names.
	 *
	 * @param names the names of the items to ignore when depositing
	 * @return <tt>true</tt> if the items were deposited, does not determine if amount was matched; otherwise <tt>false</tt>
	 */
	public boolean depositAllExcept(final String... names) {
		final List<String> whitelist = new ArrayList<String>(Arrays.asList(names));
		final Set<Integer> idsSet = new LinkedHashSet<Integer>();

		ctx.inventory.select().each(new Filter<Item>() {
			@Override
			public boolean accept(final Item item) {
				if (whitelist.contains(item.name())) {
					idsSet.add(item.id());
				}

				return true;
			}
		});

		if (ctx.inventory.isEmpty()) {
			return false;
		}

		final int[] idsArray = new int[idsSet.size()];
		int i = 0;

		for (final int id : idsSet) {
			idsArray[i++] = id;
		}

		return depositAllExcept(idsArray);
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

	private boolean check(final Item item, final int amt) {
		item.hover();
		Condition.wait(new Condition.Check() {
			@Override
			public boolean poll() {
				return ctx.menu.indexOf(new Filter<MenuCommand>() {
					@Override
					public boolean accept(final MenuCommand command) {
						return command.action.startsWith("Withdraw") || command.action.startsWith("Deposit");
					}
				}) != -1;
			}
		}, 20, 10);
		final String s = "-".concat(Integer.toString(amt)) + " ";
		for (final String a : ctx.menu.items()) {
			if (a.contains(s)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Amount
	 * An enumeration providing standard bank amount options.
	 */
	public enum Amount {
		ONE(1), FIVE(5), TEN(10), ALL_BUT_ONE(-1), ALL(0);

		private final int value;

		Amount(final int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}
}
