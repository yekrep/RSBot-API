package org.powerbot.script.rt6;

import java.lang.ref.WeakReference;

import org.powerbot.bot.rt6.client.Bundler;
import org.powerbot.bot.rt6.client.Cache;
import org.powerbot.bot.rt6.client.Client;
import org.powerbot.bot.rt6.client.HashTable;
import org.powerbot.bot.rt6.client.RSItemDef;

class ItemDefinition {
	private final WeakReference<RSItemDef> def;

	private ItemDefinition(final RSItemDef def) {
		this.def = new WeakReference<RSItemDef>(def);
	}

	static ItemDefinition getDef(final ClientContext ctx, final int id) {
		final Client client = ctx.client();
		if (client == null || id <= 0) {
			return new ItemDefinition(null);
		}
		final Bundler b = client.getItemBundler();
		final Cache cache;
		final HashTable table;
		if (b == null || (cache = b.getConfigCache()) == null || (table = cache.getTable()) == null) {
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
