package org.powerbot.script.rt6;

import org.powerbot.bot.rt6.client.Cache;
import org.powerbot.bot.rt6.client.Client;
import org.powerbot.bot.rt6.client.HashTable;
import org.powerbot.bot.rt6.client.RSItemDef;
import org.powerbot.bot.rt6.client.RSItemDefLoader;

class ItemDefinition {
	private final RSItemDef def;

	private ItemDefinition(final RSItemDef def) {
		this.def = def;
	}

	static ItemDefinition getDef(final ClientContext ctx, final int id) {
		final Client client = ctx.client();
		if (client == null || id <= 0) {
			return new ItemDefinition(null);
		}
		final RSItemDefLoader loader;
		final Cache cache;
		final HashTable table;
		if ((loader = client.getRSItemDefLoader()) == null ||
				(cache = loader.getCache()) == null || (table = cache.getTable()) == null) {
			return new ItemDefinition(null);
		}
		final Object o = org.powerbot.bot.rt6.tools.HashTable.lookup(table, id);
		return o != null && o instanceof RSItemDef ? new ItemDefinition((RSItemDef) o) : new ItemDefinition(null);
	}

	int getId() {
		return def.getID();
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
