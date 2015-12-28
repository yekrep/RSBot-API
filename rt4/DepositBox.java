package org.powerbot.script.rt4;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.powerbot.script.Condition;

/**
 * DepositBox
 * A utility class for depositing items, opening and closing a deposit box, and finding the closest usable bank deposit box.
 */
public class DepositBox extends ItemQuery<Item> {
	public DepositBox(final ClientContext ctx) {
		super(ctx);
	}

	@Override
	protected List<Item> get() {
		final List<Item> items = new ArrayList<Item>();
		if (!opened()) {
			return items;
		}

		final Component[] a = ctx.widgets.component(Constants.DEPOSITBOX_WIDGET, Constants.DEPOSITBOX_ITEMS).components();
		for (final Component c : a) {
			if (!c.valid() || c.modelZoom() == 1777) {
				break;
			}
			items.add(new Item(ctx, c));
		}

		return items;
	}

	public boolean opened() {
		return ctx.widgets.component(Constants.DEPOSITBOX_WIDGET, Constants.DEPOSITBOX_ITEMS).visible();
	}

	public boolean close() {
		return !opened() || ctx.widgets.component(Constants.DEPOSITBOX_WIDGET, 1).component(Constants.DEPOSITBOX_CLOSE).interact("Close");
	}

	public boolean depositInventory() {
		return opened() && ctx.widgets.component(Constants.DEPOSITBOX_WIDGET, Constants.DEPOSITBOX_INVENTORY).interact("Deposit inventory");
	}

	public boolean depositWornItems() {
		return opened() && ctx.widgets.component(Constants.DEPOSITBOX_WIDGET, Constants.DEPOSITBOX_WORN_ITEMS).interact("Deposit worn items");
	}

	public boolean depositLoot() {
		return opened() && ctx.widgets.component(Constants.DEPOSITBOX_WIDGET, Constants.DEPOSITBOX_LOOT).interact("Deposit loot");
	}

	public boolean deposit(final int id, final Amount amount) {
		return deposit(id, amount.getValue());
	}

	public boolean deposit(final int id, final int amount) {
		final Item item = select().id(id).shuffle().poll();
		if (amount < 0 || !item.valid()) {
			return false;
		}
		final int count = select().id(id).count(true);
		final String action;
		if (count == 1 || amount == 1) {
			action = "Deposit-1";
		} else if (amount == 0 || count <= amount) {
			action = "Deposit-All";
		} else if (amount == 5 || amount == 10) {
			action = "Deposit-" + amount;
		} else {
			action = "Deposit-X";
		}
		if (!item.interact(action)) {
			return false;
		}
		if (action.endsWith("X")) {
			if (!Condition.wait(new Condition.Check() {
				@Override
				public boolean poll() {
					return ctx.chat.pendingInput();
				}
			}, 300, 10)) {
				return false;
			}
			Condition.sleep();
			ctx.input.sendln(String.valueOf(amount));
		}
		return Condition.wait(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return count != select().id(id).count(true);
			}
		}, 300, 10);
	}

	@Override
	public Item nil() {
		return ctx.inventory.nil();
	}

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
