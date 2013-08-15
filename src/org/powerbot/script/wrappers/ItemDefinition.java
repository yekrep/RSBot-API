package org.powerbot.script.wrappers;

import java.lang.ref.WeakReference;

import org.powerbot.client.Cache;
import org.powerbot.client.Client;
import org.powerbot.client.HashTable;
import org.powerbot.client.RSItemDef;
import org.powerbot.client.RSItemDefLoader;
import org.powerbot.script.methods.MethodContext;

class ItemDefinition {
	private final WeakReference<RSItemDef> def;

	ItemDefinition(final RSItemDef def) {
		this.def = new WeakReference<>(def);
	}

	static ItemDefinition getDef(MethodContext ctx, int id) {
		Client client = ctx.getClient();
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
		final Object o = ctx.game.lookup(table, id);
		return o != null && o instanceof RSItemDef ? new ItemDefinition((RSItemDef) o) : new ItemDefinition(null);
	}

	int getId() {
		final RSItemDef def = this.def.get();
		return def != null ? def.getID() : -1;
	}

	String getName() {
		final RSItemDef def = this.def.get();
		String name = "";
		if (def != null && (name = def.getName()) == null) {
			name = "";
		}
		return name;
	}

	boolean isMembers() {
		final RSItemDef def = this.def.get();
		return def != null && def.isMembersObject();
	}

	String[] getActions() {
		final RSItemDef def = this.def.get();
		String[] actions = new String[0];
		if (def != null && (actions = def.getActions()) == null) {
			actions = new String[0];
		}
		return actions;
	}

	String[] getGroundActions() {
		final RSItemDef def = this.def.get();
		String[] actions = new String[0];
		if (def != null && (actions = def.getGroundActions()) == null) {
			actions = new String[0];
		}
		return actions;
	}
}
