package org.powerbot.game.api.wrappers.node;

import org.powerbot.game.api.Multipliers;
import org.powerbot.game.api.internal.util.Nodes;
import org.powerbot.game.api.wrappers.widget.WidgetChild;
import org.powerbot.game.bot.Bot;
import org.powerbot.game.client.CacheTable;
import org.powerbot.game.client.HardReferenceGet;
import org.powerbot.game.client.Node;
import org.powerbot.game.client.RSItem;
import org.powerbot.game.client.RSItemDefLoaderCache;
import org.powerbot.game.client.RSItemID;
import org.powerbot.game.client.RSItemInts;
import org.powerbot.game.client.RSItemStackSize;
import org.powerbot.game.client.Reference;
import org.powerbot.game.client.SoftReferenceGet;

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
		final Object data = item.getData();
		id = ((RSItemID) ((RSItemInts) data).getRSItemInts()).getRSItemID() * multipliers.ITEM_ID;
		stack = ((RSItemStackSize) ((RSItemInts) data).getRSItemInts()).getRSItemStackSize() * multipliers.ITEM_STACKSIZE;
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

	public ItemDefinition getDefinition() {
		final Object itemDefLoaderTable = Bot.resolve().client.getRSItemDefLoader();
		final Object itemDefLoaderCache = ((RSItemDefLoaderCache) itemDefLoaderTable).getRSItemDefLoaderCache();
		final Object itemDefLoader = ((CacheTable) itemDefLoaderCache).getCacheTable();
		final Node ref = Nodes.lookup(itemDefLoader, id);
		if (ref != null && ref instanceof Reference) {
			final Object reference = ((Reference) ref).getData();
			if (reference instanceof SoftReferenceGet) {
				return new ItemDefinition(((SoftReferenceGet) reference).getSoftReferenceGet());
			} else if (reference instanceof HardReferenceGet) {
				return new ItemDefinition(((HardReferenceGet) reference).getHardReferenceGet());
			}
		}
		return null;
	}
}
