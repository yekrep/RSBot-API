package org.powerbot.script.rt4;

import java.util.concurrent.Callable;

import org.powerbot.script.Condition;
import org.powerbot.script.Random;

public class Bank extends ClientAccessor {
	private static final int WIDGET = 12;
	private static final int COMPONENT_WINDOW = 0;
	private static final int COMPONENT_ITEM_CONTAINER = 6;
	private static final int COMPONENT_CLOSE = 11;
	private static final int COMPONENT_W_ITEM = 15, COMPONENT_W_NOTE = 17;
	private static final int COMPONENT_D_INVENTORY = 21, COMPONENT_D_EQUIPMENT = 23;

	public Bank(final ClientContext ctx) {
		super(ctx);
	}

	public boolean opened() {
		return ctx.widgets.widget(WIDGET).component(COMPONENT_WINDOW).visible();
	}

	public boolean close() {
		return !opened() || (ctx.widgets.widget(WIDGET).component(COMPONENT_CLOSE).interact("Close") && Condition.wait(new Callable<Boolean>() {
			@Override
			public Boolean call() {
				return !opened();
			}
		}, 30, 10));
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
		} else {
			action = "Deposit-X";
		}
		final int cache = ctx.inventory.select().count(true);
		if (!item.interact(action)) {
			return false;
		}
		if (action.endsWith("X")) {
			if (!Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					return ctx.widgets.widget(548).component(122).visible();
				}
			})) {
				return false;
			}
			Random.sleep();
			ctx.keyboard.sendln(amount + "");
		}
		return Condition.wait(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return cache != ctx.inventory.select().count(true);
			}
		});
	}


	public boolean withdrawModeNoted() {
		return ctx.varpbits.varpbit(115) == 0x1;
	}

	public boolean withdrawModeNoted(final boolean noted) {
		return withdrawModeNoted() == noted || (ctx.widgets.widget(WIDGET).component(noted ? COMPONENT_W_NOTE : COMPONENT_W_ITEM).interact(noted ? "Note" : "Item") && Condition.wait(new Callable<Boolean>() {
			@Override
			public Boolean call() {
				return withdrawModeNoted() == noted;
			}
		}, 30, 10));
	}

	public boolean depositInventory() {
		return ctx.widgets.widget(WIDGET).component(COMPONENT_D_INVENTORY).interact("Deposit");
	}

	public boolean depositEquipment() {
		return ctx.widgets.widget(WIDGET).component(COMPONENT_D_EQUIPMENT).interact("Deposit");
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
