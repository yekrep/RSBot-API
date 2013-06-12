package org.powerbot.script.internal.methods;

import org.powerbot.client.Client;
import org.powerbot.client.HashTable;
import org.powerbot.client.ItemSlot;
import org.powerbot.script.methods.ClientFactory;
import org.powerbot.script.methods.ClientLink;

public class Items extends ClientLink {
	public static final int INDEX_INVENTORY = 93;
	public static final int INDEX_EQUIPMENT = 670;
	public static final int INDEX_BANK = 95;

	public Items(ClientFactory factory) {
		super(factory);
	}

	public int[][] getItems(int index) {
		Client client = ctx.getClient();
		final HashTable table;
		if (client == null || (table = client.getItemSlots()) == null) {
			return new int[0][];
		}
		Object n = ctx.game.lookup(table, index);
		if (n == null || !(n instanceof ItemSlot)) {
			return new int[0][];
		}
		final ItemSlot slot = (ItemSlot) n;
		int[] ids = slot.getIds(), stacks = slot.getStackSizes();
		if (ids.length != stacks.length) {
			return new int[0][];
		}
		int[][] data = new int[ids.length][2];
		for (int i = 0; i < ids.length; i++) {
			data[i] = new int[]{ids[i], stacks[i]};
		}
		return data;
	}
}
