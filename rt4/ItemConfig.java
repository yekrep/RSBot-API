package org.powerbot.script.rt4;

import org.powerbot.bot.rt4.HashTable;
import org.powerbot.bot.rt4.client.Client;

class ItemConfig {
	private final org.powerbot.bot.rt4.client.ItemConfig def;

	private ItemConfig(final org.powerbot.bot.rt4.client.ItemConfig def) {
		this.def = def;
	}

	static ItemConfig getDef(final ClientContext ctx, final int id) {
		final Client client = ctx.client();
		if (client == null || id <= 0) {
			return new ItemConfig(null);
		}
		final org.powerbot.bot.rt4.client.ItemConfig o = HashTable.lookup(client.getItemConfigCache().getTable(), id, org.powerbot.bot.rt4.client.ItemConfig.class);
		return new ItemConfig(o);
	}

	String getName() {
		String name = "";
		if (def != null && (name = def.getName()) == null) {
			name = "";
		}
		return name;
	}

	boolean isMembers() {
		return def != null && def.isMembers();
	}

	String[] getActions() {
		String[] actions = new String[0];
		if (def != null && (actions = def.getActions1()) == null) {
			actions = new String[0];
		}
		return actions;
	}

	String[] getGroundActions() {
		String[] actions = new String[0];
		if (def != null && (actions = def.getActions2()) == null) {
			actions = new String[0];
		}
		return actions;
	}
}
