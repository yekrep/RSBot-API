package org.powerbot.script.rt4;

import org.powerbot.bot.rt4.Bot;
import org.powerbot.script.Identifiable;
import org.powerbot.script.Nameable;

/**
 * GenericItem
 */
abstract class GenericItem extends Interactive implements Identifiable, Nameable {
	public GenericItem(final ClientContext ctx) {
		super(ctx);
	}

	@Override
	public String name() {
		final CacheItemConfig c = CacheItemConfig.load(Bot.CACHE_WORKER, id());
		return c != null ? c.name : "";
	}

	public boolean members() {
		final CacheItemConfig c = CacheItemConfig.load(Bot.CACHE_WORKER, id());
		return c != null && c.members;
	}

	public boolean stackable() {
		final CacheItemConfig c = CacheItemConfig.load(Bot.CACHE_WORKER, id());
		return c != null && c.stackable;
	}

	public boolean noted() {
		final CacheItemConfig c = CacheItemConfig.load(Bot.CACHE_WORKER, id());
		return c != null && c.cert;
	}

	public boolean tradeable() {
		final CacheItemConfig c = CacheItemConfig.load(Bot.CACHE_WORKER, id());
		return c != null && c.tradeable;
	}

	public boolean cosmetic() {
		final CacheItemConfig c = CacheItemConfig.load(Bot.CACHE_WORKER, id());
		return c != null && c.cosmetic;
	}

	public int value() {
		final CacheItemConfig c = CacheItemConfig.load(Bot.CACHE_WORKER, id());
		return c != null ? c.value : -1;
	}

	public String[] groundActions() {
		final CacheItemConfig c = CacheItemConfig.load(Bot.CACHE_WORKER, id());
		return c != null ? c.groundActions : new String[0];
	}

	public String[] inventoryActions() {
		final CacheItemConfig c = CacheItemConfig.load(Bot.CACHE_WORKER, id());
		return c != null ? c.actions : new String[0];
	}
}
