package org.powerbot.bot.rt6;

import org.powerbot.bot.rt6.client.*;
import org.powerbot.script.rt6.*;

public class Items extends ClientAccessor {

	public Items(final ClientContext factory) {
		super(factory);
	}

	public int[][] getItems(final int index) {
		final Client client = ctx.client();
		if (client == null) {
			return new int[0][];
		}
		final ItemSlot slot = HashTable.lookup(client.getItemSlots(), index, ItemSlot.class);
		final int[] ids = slot.getIds();
		final int[] stacks = slot.getStackSizes();
		if (ids == null || stacks == null ||
				ids.length != stacks.length) {
			return new int[0][];
		}
		final int[][] data = new int[ids.length][2];
		for (int i = 0; i < ids.length; i++) {
			data[i] = new int[]{ids[i], stacks[i]};
		}
		return data;
	}
}
