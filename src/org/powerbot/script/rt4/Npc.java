package org.powerbot.script.rt4;

import java.awt.Color;
import java.lang.ref.SoftReference;

import org.powerbot.bot.rt4.client.Cache;
import org.powerbot.bot.rt4.client.Client;
import org.powerbot.bot.rt4.client.NpcConfig;
import org.powerbot.bot.rt4.client.Varbit;
import org.powerbot.bot.rt4.tools.HashTable;
import org.powerbot.script.Identifiable;

public class Npc extends Actor implements Identifiable {
	public static final Color TARGET_COLOR = new Color(255, 0, 255, 15);
	private final SoftReference<org.powerbot.bot.rt4.client.Npc> npc;
	private static final int[] lookup;

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
		this.npc = new SoftReference<org.powerbot.bot.rt4.client.Npc>(npc);
	}

	@Override
	protected org.powerbot.bot.rt4.client.Actor getActor() {
		return npc.get();
	}

	@Override
	public String name() {
		final NpcConfig config = getConfig();
		final String str = config != null ? config.getName() : "";
		return str != null ? str : "";
	}

	@Override
	public int combatLevel() {
		final NpcConfig config = getConfig();
		return config != null ? config.getLevel() : -1;
	}

	@Override
	public int id() {
		final Client client = ctx.client();
		if (client == null) {
			return -1;
		}
		final org.powerbot.bot.rt4.client.Npc npc = this.npc.get();
		final NpcConfig config = npc != null ? npc.getConfig() : null;
		if (config != null) {
			final int varbit = config.getVarbit(), si = config.getVarpbitIndex();
			int index = -1;
			if (varbit != -1) {
				final Cache cache = client.getVarbitCache();
				final Varbit varBit = (Varbit) HashTable.lookup(cache, varbit);
				if (varBit != null) {
					final int mask = lookup[varBit.getEndBit() - varBit.getStartBit()];
					index = ctx.varpbits.varpbit(varBit.getIndex()) >> varBit.getStartBit() & mask;
				}
			} else if (si != -1) {
				index = ctx.varpbits.varpbit(si);
			}
			if (index >= 0) {
				final int[] configs = config.getConfigs();
				if (configs != null && index < configs.length && configs[index] != -1) {
					return configs[index];
				}
			}
			return config.getId();
		}
		return -1;
	}

	public String[] actions() {
		final NpcConfig config = getConfig();
		final String[] arr = config != null ? config.getActions() : new String[0];
		if (arr == null) {
			return new String[0];
		}
		final String[] arr_ = new String[arr.length];
		int c = 0;
		for (final String str : arr) {
			arr_[c++] = str != null ? str : "";
		}
		return arr_;
	}

	private NpcConfig getConfig() {
		final Client client = ctx.client();
		final org.powerbot.bot.rt4.client.Npc npc = this.npc.get();
		final NpcConfig config = npc != null ? npc.getConfig() : null;
		if (client == null || config == null) {
			return null;
		}
		final int id = config.getId(), uid = id();
		if (id != uid) {
			final NpcConfig c = (NpcConfig) HashTable.lookup(client.getNpcConfigCache(), uid);
			if (c != null) {
				return c;
			}
		}
		return config;
	}

	@Override
	public boolean valid() {
		final Client client = ctx.client();
		final org.powerbot.bot.rt4.client.Npc npc = this.npc.get();
		if (client == null || npc == null) {
			return false;
		}
		final org.powerbot.bot.rt4.client.Npc[] arr = client.getNpcs();
		for (final org.powerbot.bot.rt4.client.Npc a : arr) {
			if (a == npc) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return String.format("%s[id=%d/name=%s/level=%d]",
				Npc.class.getName(), id(), name(), combatLevel());
	}
}
