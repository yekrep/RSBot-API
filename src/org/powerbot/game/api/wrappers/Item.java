package org.powerbot.game.api.wrappers;

import org.powerbot.game.api.Multipliers;
import org.powerbot.game.bot.Bot;
import org.powerbot.game.client.RSItem;
import org.powerbot.game.client.RSItemID;
import org.powerbot.game.client.RSItemInts;
import org.powerbot.game.client.RSItemStackSize;

/**
 * @author Timer
 */
public class Item {
	private final int id;
	private final int stack;
	private WidgetChild widgetChild;

	public Item(final int id, final int stack) {
		this.id = id;
		this.stack = stack;
	}

	public Item(final RSItem item) {
		final Multipliers multipliers = Bot.resolve().multipliers;
		id = ((RSItemID) ((RSItemInts) item.getData()).getRSItemInts()).getRSItemID() * multipliers.ITEM_ID;
		stack = ((RSItemStackSize) ((RSItemInts) item.getData()).getRSItemInts()).getRSItemStackSize() * multipliers.ITEM_STACKSIZE;
	}

	public Item(final WidgetChild widgetChild) {
		id = widgetChild.getChildId();
		stack = widgetChild.getChildStackSize();
		this.widgetChild = widgetChild;
	}

	public int getId() {
		return id;
	}

	public int getStackSize() {
		return stack;
	}
}
