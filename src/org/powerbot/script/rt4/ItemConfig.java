package org.powerbot.script.rt4;

import java.lang.ref.WeakReference;

import org.powerbot.bot.rt4.client.Client;
import org.powerbot.bot.rt4.tools.HashTable;

class ItemConfig {
	private final WeakReference<org.powerbot.bot.rt4.client.ItemConfig> def;

	private ItemConfig(final org.powerbot.bot.rt4.client.ItemConfig def) {
		this.def = new WeakReference<org.powerbot.bot.rt4.client.ItemConfig>(def);
	}

	static ItemConfig getDef(final ClientContext ctx, final int id) {
		final Client client = ctx.client();
		if (client == null || id <= 0) {
			return new ItemConfig(null);
		}
		final Object o = HashTable.lookup(client.getItemConfigCache(), id);
		return o instanceof org.powerbot.bot.rt4.client.ItemConfig ? new ItemConfig((org.powerbot.bot.rt4.client.ItemConfig) o) : new ItemConfig(null);
	}

	String getName() {
		final org.powerbot.bot.rt4.client.ItemConfig def = this.def.get();
		String name = "";
		if (def != null && (name = def.getName()) == null) {
			name = "";
		}
		return name;
	}

	boolean isMembers() {
		final org.powerbot.bot.rt4.client.ItemConfig def = this.def.get();
		return def != null && def.isMembers();
	}

	String[] getActions() {
		final org.powerbot.bot.rt4.client.ItemConfig def = this.def.get();
		String[] actions = new String[0];
		if (def != null && (actions = def.getActions1()) == null) {
			actions = new String[0];
		}
		return actions;
	}

	String[] getGroundActions() {
		final org.powerbot.bot.rt4.client.ItemConfig def = this.def.get();
		String[] actions = new String[0];
		if (def != null && (actions = def.getActions2()) == null) {
			actions = new String[0];
		}
		return actions;
	}
}
