package org.powerbot.script.rt4;

import java.awt.Color;
import java.io.File;

import org.powerbot.bot.cache.CacheWorker;
import org.powerbot.bot.rt4.HashTable;
import org.powerbot.bot.rt4.client.Cache;
import org.powerbot.bot.rt4.client.Client;
import org.powerbot.bot.rt4.client.NpcConfig;
import org.powerbot.bot.rt4.client.Varbit;
import org.powerbot.script.Actionable;
import org.powerbot.script.Identifiable;

public class Npc extends Actor implements Identifiable, Actionable {
	private static final CacheWorker CACHE_WORKER = new CacheWorker(new File(
			System.getProperty("user.home"), "jagexcache/oldschool/LIVE"
	));
	public static final Color TARGET_COLOR = new Color(255, 0, 255, 15);
	private final org.powerbot.bot.rt4.client.Npc npc;
	private static final int[] lookup;
	private final int hash;

	static {
		lookup = new int[32];
		int i = 2;
		for (int j = 0; j < 32; j++) {
			lookup[j] = i - 1;
			i += i;
		}
	}

	Npc(final ClientContext ctx, final org.powerbot.bot.rt4.client.Npc npc) {
		super(ctx);
		this.npc = npc;
		hash = System.identityHashCode(npc);
	}

	@Override
	protected org.powerbot.bot.rt4.client.Actor getActor() {
		return npc;
	}

	@Override
	public String name() {
		final CacheNpcConfig c = CacheNpcConfig.load(CACHE_WORKER, id());
		return c != null ? c.name : "";
	}

	@Override
	public int combatLevel() {
		final CacheNpcConfig c = CacheNpcConfig.load(CACHE_WORKER, id());
		return c != null ? c.level : -1;
	}

	@Override
	public int id() {
		final Client client = ctx.client();
		if (client == null) {
			return -1;
		}
		final NpcConfig c = npc == null ? new NpcConfig(client.reflector, null) : npc.getConfig();
		if (c.isNull()) {
			return -1;
		}
		final int varbit = c.getVarbit(), si = c.getVarpbitIndex();
		int index = -1;
		if (varbit != -1) {
			final Cache cache = client.getVarbitCache();
			final Varbit varBit = HashTable.lookup(cache.getTable(), varbit, Varbit.class);
			if (!varBit.isNull()) {
				final int mask = lookup[varBit.getEndBit() - varBit.getStartBit()];
				index = ctx.varpbits.varpbit(varBit.getIndex()) >> varBit.getStartBit() & mask;
			}
		} else if (si != -1) {
			index = ctx.varpbits.varpbit(si);
		}
		if (index >= 0) {
			final int[] configs = c.getConfigs();
			if (index < configs.length && configs[index] != -1) {
				return configs[index];
			}
		}
		return c.getId();
	}

	@Override
	public String[] actions() {
		final CacheNpcConfig c = CacheNpcConfig.load(CACHE_WORKER, id());
		return c != null ? c.actions : new String[0];
	}

	@Override
	public boolean valid() {
		final Client client = ctx.client();
		if (client == null || npc.obj.get() == null) {
			return false;
		}
		final org.powerbot.bot.rt4.client.Npc[] arr = client.getNpcs();
		for (final org.powerbot.bot.rt4.client.Npc a : arr) {
			if (npc.equals(a)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return hash;
	}

	@Override
	public String toString() {
		return String.format("%s[id=%d/name=%s/level=%d]",
				Npc.class.getName(), id(), name(), combatLevel());
	}
}
