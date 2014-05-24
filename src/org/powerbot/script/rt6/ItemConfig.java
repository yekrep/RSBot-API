package org.powerbot.script.rt6;

import org.powerbot.bot.rt6.client.Cache;
import org.powerbot.bot.rt6.client.Client;
import org.powerbot.bot.rt6.client.HashTable;
import org.powerbot.bot.rt6.client.RSItemDef;
import org.powerbot.bot.rt6.client.RSItemDefLoader;

class ItemConfig {
	private final RSItemDef def;

	private ItemConfig(final RSItemDef def) {
		this.def = def;
	}

	static ItemConfig getConfig(final ClientContext ctx, final int id) {
		final Client client = ctx.client();
		if (client == null || id <= 0) {
			return new ItemConfig(null);
		}
		final RSItemDefLoader loader;
		final Cache cache;
		final HashTable table;
		if ((loader = client.getRSItemDefLoader()) == null ||
				(cache = loader.getCache()) == null || (table = cache.getTable()) == null) {
			return new ItemConfig(new RSItemDef(client.reflector, null));
		}
		final Object o = org.powerbot.bot.rt6.tools.HashTable.lookup(table, id);
		return new ItemConfig(new RSItemDef(client.reflector, o));
	}

	int getId() {
		return def != null ? def.getID() : -1;
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
