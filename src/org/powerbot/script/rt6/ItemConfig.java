package org.powerbot.script.rt6;

import org.powerbot.bot.rt6.client.Client;
import org.powerbot.bot.rt6.client.HashTable;

class ItemConfig {
	private final org.powerbot.bot.rt6.client.ItemConfig def;

	private ItemConfig(final org.powerbot.bot.rt6.client.ItemConfig def) {
		this.def = def;
	}

	static ItemConfig getConfig(final ClientContext ctx, final int id) {
		final Client client = ctx.client();
		if (client == null || id <= 0) {
			return new ItemConfig(null);
		}
		final HashTable table = client.getItemBundler().getConfigCache().getTable();
		if (table.isNull()) {
			return new ItemConfig(new org.powerbot.bot.rt6.client.ItemConfig(client.reflector, null));
		}
		return new ItemConfig(org.powerbot.bot.rt6.HashTable.lookup(table, id, org.powerbot.bot.rt6.client.ItemConfig.class));
	}

	int getId() {
		return def != null ? def.getId() : -1;
	}

	String getName() {
		String name = "";
		if (def != null && (name = def.getName()) == null) {
			name = "";
		}
		return name;
	}

	boolean isMembers() {
		return def != null && def.isMembersObject();
	}

	String[] getActions() {
		String[] actions = new String[0];
		if (def != null && (actions = def.getActions()) == null) {
			actions = new String[0];
		}
		return actions;
	}

	String[] getGroundActions() {
		String[] actions = new String[0];
		if (def != null && (actions = def.getGroundActions()) == null) {
			actions = new String[0];
		}
		return actions;
	}
}
