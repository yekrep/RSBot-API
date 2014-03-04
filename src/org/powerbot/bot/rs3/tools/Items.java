package org.powerbot.bot.rs3.tools;

import org.powerbot.bot.rs3.client.Client;
import org.powerbot.bot.rs3.client.HashTable;
import org.powerbot.bot.rs3.client.ItemSlot;
import org.powerbot.script.rs3.tools.ClientAccessor;
import org.powerbot.script.rs3.tools.ClientContext;

public class Items extends ClientAccessor {
	public static final int INDEX_INVENTORY = 93;
	public static final int INDEX_MONEY_POUCH = 623;
	public static final int INDEX_EQUIPMENT = 670;
	public static final int INDEX_BANK = 95;

	public Items(final ClientContext factory) {
		super(factory);
	}

	public int[][] getItems(final int index) {
		final Client client = ctx.getClient();
		final HashTable table;
		if (client == null || (table = client.getItemSlots()) == null) {
			return new int[0][];
		}
		final Object n = ctx.game.lookup(table, index);
		if (n == null || !(n instanceof ItemSlot)) {
			return new int[0][];
		}
		final ItemSlot slot = (ItemSlot) n;
		final int[] ids = slot.getIds();
		final int[] stacks = slot.getStackSizes();
		if (ids.length != stacks.length) {
			return new int[0][];
		}
		final int[][] data = new int[ids.length][2];
		for (int i = 0; i < ids.length; i++) {
			data[i] = new int[]{ids[i], stacks[i]};
		}
		return data;
	}
}
