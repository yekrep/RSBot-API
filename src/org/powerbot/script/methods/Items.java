package org.powerbot.script.methods;

import org.powerbot.bot.World;
import org.powerbot.client.Client;
import org.powerbot.client.HashTable;
import org.powerbot.client.ItemSlot;
import org.powerbot.script.wrappers.Item;

public class Items {//you might want to move/hide this class
	public static final int INDEX_INVENTORY = 93;
	public static final int INDEX_EQUIPMENT = 670;
	public static final int INDEX_BANK = 95;

	private static Item[] getItems(int index) {
		final Client client = World.getWorld().getClient();
		final HashTable table;
		if (client == null || (table = client.getItemSlots()) == null) return new Item[0];
		Object n = Game.lookup(table, index);
		if (n == null || !(n instanceof ItemSlot)) return new Item[0];
		final ItemSlot slot = (ItemSlot) n;
		int[] ids = slot.getIds(), stacks = slot.getStackSizes();
		if (ids.length != stacks.length) return new Item[0];
		Item[] items = new Item[ids.length];
		for (int i = 0; i < ids.length; i++) items[i] = new Item(ids[i], stacks[i]);
		return items;
	}
}
