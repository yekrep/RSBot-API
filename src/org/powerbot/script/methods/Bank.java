package org.powerbot.script.methods;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import org.powerbot.script.lang.AbstractQuery;
import org.powerbot.script.lang.Filter;
import org.powerbot.script.lang.ItemQuery;
import org.powerbot.script.util.Condition;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.Component;
import org.powerbot.script.wrappers.GameObject;
import org.powerbot.script.wrappers.Interactive;
import org.powerbot.script.wrappers.Item;
import org.powerbot.script.wrappers.Locatable;
import org.powerbot.script.wrappers.Npc;
import org.powerbot.script.wrappers.Tile;
import org.powerbot.script.wrappers.Widget;

/**
 * Utilities pertaining to the bank.
 *
 * @author Timer
 */
public class Bank extends ItemQuery<Item> {
	public static final int[] BANK_NPC_IDS = new int[]{
			44, 45, 166, 494, 495, 496, 497, 498, 499, 553, 909, 953, 958, 1036, 1360, 1702, 2163, 2164, 2354, 2355,
			2568, 2569, 2570, 2617, 2618, 2619, 2718, 2759, 3046, 3198, 3199, 3293, 3416, 3418, 3824, 4456, 4457,
			4458, 4459, 4519, 4907, 5257, 5258, 5259, 5260, 5488, 5776, 5777, 5901, 6200, 6362, 7049, 7050, 7605,
			8948, 9710, 13932, 14707, 14923, 14924, 14925, 15194, 16603, 16602
	};
	public static final int[] BANK_BOOTH_IDS = new int[]{
			782, 2213, 3045, 5276, 6084, 10517, 11338, 11758, 12759, 12798, 12799, 14369, 14370,
			16700, 19230, 20325, 20326, 20327, 20328, 22819, 24914, 25808, 26972, 29085, 34752, 35647,
			36262, 36786, 37474, 49018, 49019, 52397, 52589, 76274, 69024, 69023, 69022,
			83954
	};
	public static final int[] BANK_COUNTER_IDS = new int[]{
			42217, 42377, 42378, 2012, 66665, 66666, 66667
	};
	public static final int[] BANK_CHEST_IDS = new int[]{
			2693, 4483, 8981, 12308, 14382, 20607, 21301, 27663, 42192, 57437, 62691, 83634, 81756
	};
	public static final Tile[] UNREACHABLE_BANK_TILES = new Tile[]{
			new Tile(3191, 3445, 0), new Tile(3180, 3433, 0)
	};
	private static final Filter<Interactive> UNREACHABLE_FILTER = new Filter<Interactive>() {
		@Override
		public boolean accept(Interactive interactive) {
			if (interactive instanceof Locatable) {
				Tile tile = ((Locatable) interactive).getLocation();
				for (Tile bad : UNREACHABLE_BANK_TILES) {
					if (tile.equals(bad)) {
						return false;
					}
				}
			}
			return true;
		}
	};
	public static final int WIDGET = 762;
	public static final int COMPONENT_BUTTON_CLOSE = 50;
	public static final int COMPONENT_CONTAINER_ITEMS = 39;
	public static final int COMPONENT_BUTTON_WITHDRAW_MODE = 8;
	public static final int COMPONENT_BUTTON_DEPOSIT_INVENTORY = 12;
	public static final int COMPONENT_BUTTON_DEPOSIT_MONEY = 14;
	public static final int COMPONENT_BUTTON_DEPOSIT_EQUIPMENT = 16;
	public static final int COMPONENT_BUTTON_DEPOSIT_FAMILIAR = 18;
	public static final int COMPONENT_SCROLL_BAR = 40;
	public static final int SETTING_BANK_STATE = 110;
	public static final int SETTING_WITHDRAW_MODE = 160;

	public Bank(MethodContext factory) {
		super(factory);
	}

	private Interactive getBank() {
		final Filter<Interactive> f = Interactive.areOnScreen();
		List<Interactive> interactives = new ArrayList<>();
		Npc n;
		GameObject o;

		ctx.npcs.select().id(BANK_NPC_IDS).select(f).select(UNREACHABLE_FILTER).nearest();
		ctx.npcs.limit(3).within(n = ctx.npcs.poll(), 1d).first().addTo(interactives);
		interactives.add(n);

		final List<GameObject> cache = new ArrayList<>();
		ctx.objects.select().select(f).select(UNREACHABLE_FILTER).nearest().addTo(cache);

		ctx.objects.id(BANK_BOOTH_IDS).limit(3).within(o = ctx.objects.poll(), 1d).first().addTo(interactives);
		interactives.add(o);

		ctx.objects.select(cache).id(BANK_COUNTER_IDS).limit(3).within(o = ctx.objects.poll(), 1d).first().addTo(interactives);
		interactives.add(o);

		ctx.objects.select(cache).id(BANK_CHEST_IDS).limit(3).within(o = ctx.objects.poll(), 1d).first().addTo(interactives);
		interactives.add(o);

		return interactives.isEmpty() ? ctx.objects.getNil() : interactives.get(Random.nextInt(0, interactives.size()));
	}

	/**
	 * Returns the absolute nearest bank for walking purposes. Do not use this to open the bank.
	 *
	 * @return the {@link Locatable} of the nearest bank or {@link Tile#NIL}
	 * @see #open()
	 */
	public Locatable getNearest() {
		Locatable nearest = ctx.npcs.select().select(UNREACHABLE_FILTER).id(BANK_NPC_IDS).nearest().limit(1).poll();

		Tile loc = ctx.players.local().getLocation();
		for (GameObject object : ctx.objects.select().select(UNREACHABLE_FILTER).
				id(BANK_BOOTH_IDS, BANK_COUNTER_IDS, BANK_CHEST_IDS).nearest().limit(1)) {
			if (loc.distanceTo(object) < loc.distanceTo(nearest)) {
				nearest = object;
			}
		}
		return nearest;
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
	 * Determines if a bank is on screen.
	 *
	 * @return <tt>true</tt> if a bank is in view; otherwise <tt>false</tt>
	 */
	public boolean isOnScreen() {
		return getBank().isValid();
	}

	/**
	 * Determines if the bank is open.
	 *
	 * @return <tt>true</tt> is the bank is open; otherwise <tt>false</tt>
	 */
	public boolean isOpen() {
		return ctx.widgets.get(WIDGET, COMPONENT_CONTAINER_ITEMS).isValid();
	}

	/**
	 * Opens a random on-screen bank.
	 * <p/>
	 * Do not continue execution within the current poll after this method so BankPin may activate.
	 *
	 * @return <tt>true</tt> if the bank was opened; otherwise <tt>false</tt>
	 */
	public boolean open() {
		if (isOpen()) {
			return true;
		}
		Interactive interactive = getBank();
		final int id;
		if (interactive.isValid()) {
			if (interactive instanceof Npc) {
				id = ((Npc) interactive).getId();
			} else if (interactive instanceof GameObject) {
				id = ((GameObject) interactive).getId();
			} else {
				id = -1;
			}
		} else {
			id = -1;
		}
		if (id == -1) {
			return false;
		}
		int index = -1;
		final int[][] ids = {BANK_NPC_IDS, BANK_BOOTH_IDS, BANK_CHEST_IDS, BANK_COUNTER_IDS};
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
			if (interactive.hover()) {
				sleep(50, 100);
			}
			actions[index] = ctx.menu.indexOf(Menu.filter("Open")) != -1 ? "Open" : ctx.menu.indexOf(Menu.filter("Use")) != -1 ? "Use" : null;
			if (actions[index] == null) {
				return false;
			}
		}
		if (interactive.interact(actions[index], options[index])) {
			final Widget bankPin = ctx.widgets.get(13);
			for (int i = 0; i < 20 && !isOpen() && !bankPin.isValid(); i++) {
				sleep(200, 300);
			}
		}
		return isOpen();
	}

	/**
	 * Closes the bank by means of walking or the 'X'.
	 *
	 * @return <tt>true</tt> if the bank was closed; otherwise <tt>false</tt>
	 */
	public boolean close() {
		if (!isOpen()) {
			return true;
		}

		Component c = ctx.widgets.get(WIDGET, COMPONENT_BUTTON_CLOSE);
		return c.interact("Close") & Condition.wait(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return !isOpen();
			}
		}, Random.nextInt(100, 200), 10);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<Item> get() {
		final Component c = ctx.widgets.get(WIDGET, COMPONENT_CONTAINER_ITEMS);
		if (c == null || !c.isValid()) {
			return new ArrayList<>();
		}
		final Component[] components = c.getChildren();
		List<Item> items = new ArrayList<>(components.length);
		for (final Component i : components) {
			if (i.getItemId() != -1) {
				items.add(new Item(ctx, i));
			}
		}
		return items;
	}

	/**
	 * Grabs the {@link Item} at the provided index.
	 *
	 * @param index the index of the item to grab
	 * @return the {@link Item} at the specified index; or {@link org.powerbot.script.methods.Bank#getNil()}
	 */
	public Item getItemAt(final int index) {
		final Component i = ctx.widgets.get(WIDGET, COMPONENT_CONTAINER_ITEMS).getChild(index);
		if (i.getItemId() != -1) {
			return new Item(ctx, i);
		}
		return getNil();
	}

	/**
	 * Returns the first index of the provided item id.
	 *
	 * @param id the id of the item
	 * @return the index of the item; otherwise {@code -1}
	 */
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
	 * @return the index of the current bank tab
	 */
	public int getCurrentTab() {
		return ((ctx.settings.get(SETTING_BANK_STATE) >>> 24) - 136) / 8;
	}

	/**
	 * Changes the current tab to the provided index.
	 *
	 * @param index the index desired
	 * @return <tt>true</tt> if the tab was successfully changed; otherwise <tt>false</tt>
	 */
	public boolean setCurrentTab(final int index) {
		Component c = ctx.widgets.get(WIDGET, 35 - (index * 2));
		return c.click() && Condition.wait(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return getCurrentTab() == index;
			}
		}, 100, 8);
	}

	/**
	 * Returns the item in the specified tab if it exists.
	 *
	 * @param index the tab index
	 * @return the {@link Item} displayed in the tab; otherwise {@link org.powerbot.script.methods.Bank#getNil()}
	 */
	public Item getTabItem(final int index) {
		final Component c = ctx.widgets.get(WIDGET, 82 - (index * 2));
		if (c != null && c.isValid()) {
			return new Item(ctx, c);
		}
		return getNil();
	}

	/**
	 * Withdraws an item with the provided id and amount.
	 *
	 * @param id     the id of the item
	 * @param amount the amount to withdraw
	 * @return <tt>true</tt> if the item was withdrew, does not determine if amount was matched; otherwise <tt>false</tt>
	 */
	public boolean withdraw(int id, Amount amount) {
		return withdraw(id, amount.getValue());
	}

	/**
	 * Withdraws an item with the provided id and amount.
	 *
	 * @param id     the id of the item
	 * @param amount the amount to withdraw
	 * @return <tt>true</tt> if the item was withdrew, does not determine if amount was matched; otherwise <tt>false</tt>
	 */
	public boolean withdraw(int id, int amount) {//TODO: anti pattern
		Item item = select().id(id).poll();
		final Component container = ctx.widgets.get(WIDGET, COMPONENT_CONTAINER_ITEMS);
		if (!item.isValid() || !container.isValid()) {
			return false;
		}

		final Component c = item.getComponent();
		Point p = c.getRelativeLocation();
		if (p.y == 0) {
			for (int i = 0; i < 5 && getCurrentTab() != 0; i++) {
				if (!setCurrentTab(0)) {
					sleep(100, 200);
				}
			}
		}
		if (c.getRelativeLocation().y == 0) {
			return false;
		}
		final Rectangle bounds = container.getViewportRect();
		final Component scroll = ctx.widgets.get(WIDGET, COMPONENT_SCROLL_BAR);
		if (scroll == null || bounds == null) {
			return false;
		}
		if (!bounds.contains(c.getBoundingRect())) {
			if (ctx.widgets.scroll(c, scroll, bounds.contains(ctx.mouse.getLocation()))) {
				sleep(200, 400);
			}
			if (!bounds.contains(c.getBoundingRect())) {
				return false;
			}
		}
		String action = "Withdraw-" + amount;
		if (amount == 0 ||
				(item.getStackSize() <= amount && amount != 1 && amount != 5 && amount != 10)) {
			action = "Withdraw-All";
		} else if (amount == -1 || amount == (item.getStackSize() - 1)) {
			action = "Withdraw-All but one";
		}

		final int inv = ctx.backpack.getMoneyPouch() + ctx.backpack.select().count(true);
		if (containsAction(c, action)) {
			if (!c.interact(action)) {
				return false;
			}
		} else {
			if (!c.interact("Withdraw-X")) {
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
		for (int i = 0; i < 25 && ctx.backpack.getMoneyPouch() + ctx.backpack.select().count(true) == inv; i++) {
			sleep(100, 200);
		}
		return ctx.backpack.getMoneyPouch() + ctx.backpack.select().count(true) != inv;
	}

	/**
	 * Deposits an item with the provided id and amount.
	 *
	 * @param id     the id of the item
	 * @param amount the amount to deposit
	 * @return <tt>true</tt> if the item was deposited, does not determine if amount was matched; otherwise <tt>false</tt>
	 */
	public boolean deposit(int id, Amount amount) {
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
		Item item = ctx.backpack.select().id(id).shuffle().poll();
		if (!isOpen() || amount < 0 || !item.isValid()) {
			return false;
		}

		String action = "Deposit-" + amount;
		final int c = ctx.backpack.select().id(item.getId()).count(true);
		if (c == 1) {
			action = "Deposit";
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
		return ctx.widgets.get(WIDGET, COMPONENT_BUTTON_DEPOSIT_MONEY).click();
	}

	/**
	 * Changes the withdraw mode.
	 *
	 * @param noted <tt>true</tt> for noted items; otherwise <tt>false</tt>
	 * @return <tt>true</tt> if the withdraw mode was successfully changed; otherwise <tt>false</tt>
	 */
	public boolean setWithdrawMode(final boolean noted) {
		if (isWithdrawModeNoted() != noted) {
			final Component c = ctx.widgets.get(WIDGET, COMPONENT_BUTTON_WITHDRAW_MODE);
			if (c != null && c.isValid() && c.click(true)) {
				for (int i = 0; i < 20 && isWithdrawModeNoted() != noted; i++) {
					sleep(100, 200);
				}
			}
		}
		return isWithdrawModeNoted() == noted;
	}

	/**
	 * Determines if the withdraw mode is noted mode.
	 *
	 * @return <tt>true</tt> if withdrawing as notes; otherwise <tt>false</tt>
	 */
	public boolean isWithdrawModeNoted() {
		return ctx.settings.get(SETTING_WITHDRAW_MODE) == 0x1;
	}

	private boolean containsAction(final Component c, String action) {
		action = action.toLowerCase();
		final String[] actions = c.getActions();
		if (action == null) {
			return false;
		}
		for (final String a : actions) {
			if (a != null && a.toLowerCase().contains(action)) {
				return true;
			}
		}
		return false;
	}

	private boolean isInputWidgetOpen() {
		final Component child = ctx.widgets.get(1469, 1);
		return child != null && child.isVisible();
	}

	/**
	 * {@inheritDoc}
	 */
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
